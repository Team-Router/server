package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.route.RouteRequest.GetDirectionRequest;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RouteService {

    @Value("${SEOUL_API_KEY}")
    private String SEOUL_API_KEY;
    private final Response response;
    private final StationRepository stationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ExecutorService executorService;
    private final RouteClient routeClient;
    private final ObjectMapper objectMapper;

    public RouteService(Response response, StationRepository stationRepository,
                        RedisTemplate<String, String> redisTemplate, RouteClient routeClient, ObjectMapper objectMapper) {
        this.response = response;
        this.stationRepository = stationRepository;
        this.redisTemplate = redisTemplate;
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

    public ResponseEntity<?> getStation() {
        try {
            Map<String, String> stationData = new HashMap<>();
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Set<String> keys = redisTemplate.keys("*");
            if (keys.isEmpty()) {
                return response.fail("No station data", HttpStatus.BAD_REQUEST);
            }
            for (String key : keys) {
                stationData.put(key, valueOperations.get(key));
            }
            return response.success(stationData);
        } catch (Exception e) {
            e.printStackTrace();
            return response.fail("Failed to retrieve station data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> updateStation() {
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String BIKE_PATH = "/json/bikeList";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

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
                        valueOperations.multiSet(stations);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, executorService));
            } catch (MalformedURLException e) {
                return response.fail("Malformed URL", HttpStatus.BAD_REQUEST);
            }
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get();
            return response.success("Update station success");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return response.fail("Update station failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getDirection(GetDirectionRequest getDirectionRequest) {
        double startLatitude = getDirectionRequest.getStartLocation().latitude();
        double startLongitude = getDirectionRequest.getStartLocation().longitude();
        double endLatitude = getDirectionRequest.getEndLocation().latitude();
        double endLongitude = getDirectionRequest.getEndLocation().longitude();

        Station startStation = stationRepository.findNearestStation(startLatitude, startLongitude);
        Station endStation = stationRepository.findNearestStation(endLatitude, endLongitude);

        String CYCLE_PROFILE = "cycling";
        String WALKING_PROFILE = "walking";
        String[] PROFILE = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};

        String[] COORDINATES = {
                "/" + startLongitude + "," + startLatitude + ";" + startStation.getStationLongitude() + ","
                        + startStation.getStationLatitude(),
                "/" + startStation.getStationLongitude() + "," + startStation.getStationLatitude() + ";"
                        + endStation.getStationLongitude() + "," + endStation.getStationLatitude(),
                "/" + endStation.getStationLongitude() + "," + endStation.getStationLatitude() + ";" + endLongitude
                        + "," + endLatitude
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
