package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.route.RouteRequest.CycleRequest;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

@Service
public class RouteService {

    @Value("${SEOUL_API_KEY}")
    private String SEOUL_API_KEY;

    @Value("${MAPBOX_API_KEY}")
    private String MAPBOX_API_KEY;

    private final Response response;
    private final StationRepository stationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ExecutorService executorService;

    public RouteService(Response response, StationRepository stationRepository,
                        RedisTemplate<String, String> redisTemplate) {
        this.response = response;
        this.stationRepository = stationRepository;
        this.redisTemplate = redisTemplate;
        this.executorService = Executors.newFixedThreadPool(3);
    }

    public ResponseEntity<?> getStation() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys == null) {
            return response.fail("No station data", HttpStatus.BAD_REQUEST);
        }

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Map<String, String> stations = new HashMap<>();
        for (String key : keys) {
            stations.put(key, valueOperations.get(key));
        }
        return response.success(stations);
    }

    public ResponseEntity<?> updateStation() {
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String API_KEY = SEOUL_API_KEY;
        String BIKE_PATH = "/json/bikeList";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String target : TARGET_LIST) {
            URL MASTER_URL;
            try {
                MASTER_URL = new URL(BASE_URL + API_KEY + BIKE_PATH + target);
            } catch (MalformedURLException e) {
                return response.fail("Malformed URL", HttpStatus.BAD_REQUEST);
            }
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
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return response.success("update station success");
    }

    public ResponseEntity<?> cycleV1(CycleRequest cycleRequest) throws MalformedURLException {
        double startLatitude = Double.parseDouble(cycleRequest.getStartLatitude());
        double startLongitude = Double.parseDouble(cycleRequest.getStartLongitude());
        double endLatitude = Double.parseDouble(cycleRequest.getEndLatitude());
        double endLongitude = Double.parseDouble(cycleRequest.getEndLongitude());

//        double startNearDistance = Double.MAX_VALUE;
//        long startNearStationId = 0;
//        double endNearDistance = Double.MAX_VALUE;
//        long endNearStationId = 0;
//
//        List<Station> allStations = stationRepository.findAll();
//        for (Station station : allStations) {
//            double latitude = station.getStationLatitude();
//            double longitude = station.getStationLongitude();
//
//            double startDistance = Math.abs(startLatitude - latitude) + Math.abs(startLongitude - longitude);
//            double endDistance = Math.abs(endLatitude - latitude) + Math.abs(endLongitude - longitude);
//
//            if (startDistance < startNearDistance) {
//                startNearDistance = startDistance;
//                startNearStationId = station.getId();
//            }
//            if (endDistance < endNearDistance) {
//                endNearDistance = endDistance;
//                endNearStationId = station.getId();
//            }
//        }
//        Station startStation = stationRepository.findById(startNearStationId).get();
//        Station endStation = stationRepository.findById(endNearStationId).get();

        Station startStation = stationRepository.findNearestStation(startLatitude, startLongitude);
        Station endStation = stationRepository.findNearestStation(endLatitude, endLongitude);

        String BASE_URL = "https://api.mapbox.com/directions/v5";
        String CYCLE_PROFILE = "/mapbox/cycling";
        String WALKING_PROFILE = "/mapbox/walking";
        String[] PROFILE = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};
        String GEOJSON = "?geometries=geojson";
        String LANGUAGE = "&language=ko";
        String STEPS = "&steps=true";
        String[] COORDINATES = {
                "/" + startLongitude + "," + startLatitude + ";" + startStation.getStationLongitude() + ","
                        + startStation.getStationLatitude(),
                "/" + startStation.getStationLongitude() + "," + startStation.getStationLatitude() + ";"
                        + endStation.getStationLongitude() + "," + endStation.getStationLatitude(),
                "/" + endStation.getStationLongitude() + "," + endStation.getStationLatitude() + ";" + endLongitude
                        + "," + endLatitude
        };
        String ACCESS_TOKEN = MAPBOX_API_KEY;

        for (int i = 0; i < 3; i++) {
            URL MASTER_URL = new URL(
                    BASE_URL + PROFILE[i] + COORDINATES[i] + GEOJSON + ACCESS_TOKEN + LANGUAGE + STEPS);
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8));
                String result = br.readLine();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result).get("routes");
                JsonNode jsonNode1 = objectMapper.readTree(result).get("waypoints");
//                for (JsonNode node : jsonNode1) {
//                    System.out.println(node.get("location").toString());
//                }
//                for (JsonNode node : jsonNode) {
//                    System.out.println(node.get("geometry").toString());
//                }
                // TODO response 만들기
            } catch (Exception e) {
                return response.fail(String.valueOf(e), HttpStatus.BAD_REQUEST);
            }
        }
        return response.success();
    }
}
