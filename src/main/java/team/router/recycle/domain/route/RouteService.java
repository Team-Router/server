package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.route.model.GetDirectionResponseDeserializer;
import team.router.recycle.domain.route.model.RouteRequest;
import team.router.recycle.domain.route.model.RouteResponse;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RouteService {

    private static final String WALKING_PROFILE = "walking";
    private static final String CYCLE_PROFILE = "cycling";
    private final Response response;
    private final StationRepository stationRepository;
    private final ExecutorService executorService;
    private final RouteClient routeClient;
    private final ObjectMapper objectMapper;
    @Value("${SEOUL_API_KEY}")
    private String SEOUL_API_KEY;

    public RouteService(Response response, StationRepository stationRepository, RouteClient routeClient, ObjectMapper objectMapper) {
        this.response = response;
        this.stationRepository = stationRepository;
        this.routeClient = routeClient;
        this.objectMapper = objectMapper;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        registerCustomModule();
    }

    private void registerCustomModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(RouteResponse.getDirectionResponse.class, new GetDirectionResponseDeserializer());
        objectMapper.registerModule(module);
    }

    // update_redis 삭제 버전
    public Map<String, String> updateStation() {
        Map<String, String> stationMap = new HashMap<>();
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String BIKE_PATH = "/json/bikeList";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String target : TARGET_LIST) {
            try {
                URL MASTER_URL = new URL(BASE_URL + SEOUL_API_KEY + BIKE_PATH + target);
                futures.add(CompletableFuture.runAsync(() -> {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8))) {
                        String result = br.readLine();
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(result).get("rentBikeStatus").get("row");
                        Map<String, String> stations = new HashMap<>();
                        for (JsonNode station : jsonNode) {
                            String stationId = station.get("stationId").asText();
                            String parkingBikeTotCnt = station.get("parkingBikeTotCnt").asText();
                            stations.put(stationId, parkingBikeTotCnt);
                        }
                        stationMap.putAll(stations);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, executorService));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allFutures.get();
            return stationMap;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("errorCode", "400");
            error.put("errorMessage", "InterruptedException | ExecutionException");
            return error;
        }
    }

    public ResponseEntity<?> getWalkDirection(RouteRequest.GetDirectionRequest getDirectionRequest) {
        double startLatitude = getDirectionRequest.getStartLocation().latitude();
        double startLongitude = getDirectionRequest.getStartLocation().longitude();
        double endLatitude = getDirectionRequest.getEndLocation().latitude();
        double endLongitude = getDirectionRequest.getEndLocation().longitude();

        RouteResponse.getDirectionResponse getDirectionResponse = new RouteResponse.getDirectionResponse();
        String coordinates = "/" + startLongitude + "," + startLatitude + ";"
                + endLongitude + "," + endLatitude;

        try {
            String result = routeClient.getRouteInfo(WALKING_PROFILE, coordinates);
            JsonNode node = objectMapper.readTree(result).get("routes").get(0);
            getDirectionResponse = objectMapper.treeToValue(node, RouteResponse.getDirectionResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.success(getDirectionResponse);
    }

    public ResponseEntity<?> getCycleDirection(RouteRequest.GetDirectionRequest getDirectionRequest) {
        Map<String, String> stationsMap = updateStation();
        if ("400".equals(stationsMap.get("errorCode"))) {
            return response.success(stationsMap.get("errorMessage"));
        }

        double startLatitude = getDirectionRequest.getStartLocation().latitude();
        double startLongitude = getDirectionRequest.getStartLocation().longitude();
        double endLatitude = getDirectionRequest.getEndLocation().latitude();
        double endLongitude = getDirectionRequest.getEndLocation().longitude();
        Station startStation = null;
        List<Station> nearestStations = stationRepository.findNearestStation(startLatitude, startLongitude, 3);
        for (Station nearestStation : nearestStations) {
            String stationId = nearestStation.getStationId();
            String stationBikeCnt = stationsMap.get(stationId);

            if ("0".equals(stationBikeCnt)) {
                System.out.println(stationId);
                continue;
            }
            startStation = nearestStation;
            break;
        }
        if (startStation == null) {
            return response.success("현재 대여 가능한 자전거가 없습니다.");
        }
        Station endStation = stationRepository.findNearestStation(endLatitude, endLongitude, 1).get(0);

        String[] PROFILE = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};
        String[] COORDINATES = {
                "/" + startLongitude + "," + startLatitude + ";"
                        + startStation.getStationLongitude() + "," + startStation.getStationLatitude(),
                "/" + startStation.getStationLongitude() + "," + startStation.getStationLatitude() + ";"
                        + endStation.getStationLongitude() + "," + endStation.getStationLatitude(),
                "/" + endStation.getStationLongitude() + "," + endStation.getStationLatitude() + ";"
                        + endLongitude + "," + endLatitude
        };

        List<RouteResponse.getDirectionResponse> getDirectionResponses = new ArrayList<>();

        for (int i = 0; i < PROFILE.length; i++) {
            try {
                String result = routeClient.getRouteInfo(PROFILE[i], COORDINATES[i]);
                JsonNode node = objectMapper.readTree(result).get("routes").get(0);
                getDirectionResponses.add(objectMapper.treeToValue(node, RouteResponse.getDirectionResponse.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response.success(getDirectionResponses);
    }
}
