package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.route.RouteRequest.getDirectionRequest;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public ResponseEntity<?> getStation() {
        try (Cursor<byte[]> cursor = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().scan(ScanOptions.scanOptions().match("*").build())) {
            Map<String, String> stationData = new HashMap<>();
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

            while (cursor.hasNext()) {
                byte[] keyBytes = cursor.next();
                String key = new String(keyBytes, StandardCharsets.UTF_8);
                String value = valueOperations.get(key);
                stationData.put(key, value);
            }

            if (stationData.isEmpty()) {
                return response.fail("No station data", HttpStatus.BAD_REQUEST);
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

    public ResponseEntity<?> cycleV1(getDirectionRequest getDirectionRequest) {
        double startLatitude = Double.parseDouble(getDirectionRequest.getStartLatitude());
        double startLongitude = Double.parseDouble(getDirectionRequest.getStartLongitude());
        double endLatitude = Double.parseDouble(getDirectionRequest.getEndLatitude());
        double endLongitude = Double.parseDouble(getDirectionRequest.getEndLongitude());

        Station startStation = stationRepository.findNearestStation(startLatitude, startLongitude);
        Station endStation = stationRepository.findNearestStation(endLatitude, endLongitude);

        String BASE_URL = "https://api.mapbox.com/directions/v5";
        String CYCLE_PROFILE = "/mapbox/cycling";
        String WALKING_PROFILE = "/mapbox/walking";
        String[] PROFILE = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};
        String GEOJSON = "?geometries=geojson";
        String ACCESS = "&access_token=";
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
            URL url;
            try {
                url = new URL(BASE_URL + PROFILE[i] + COORDINATES[i] + GEOJSON + ACCESS + MAPBOX_API_KEY);
            } catch (MalformedURLException e) {
                return response.fail("Malformed URL", HttpStatus.BAD_REQUEST);
            }

            try (InputStream inputStream = url.openStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String result = br.readLine();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result).get("routes").get(0);
                RouteResponse.getDirectionResponse getDirectionResponse = new RouteResponse.getDirectionResponse();
                getDirectionResponse.setRoutingProfile(RoutingProfile.valueOf(jsonNode.get("weight_name").asText()));
                getDirectionResponse.setDistance(new Distance(jsonNode.get("distance").asInt()));
                getDirectionResponse.setDuration(new Duration(jsonNode.get("duration").asInt()));
                for (JsonNode coordinate : jsonNode.get("geometry").get("coordinates")) {
                    getDirectionResponse.getLocations().add(new Location(coordinate.get(1).asDouble(),
                            coordinate.get(0).asDouble()));
                }
                getDirectionResponses.add(getDirectionResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response.success(getDirectionResponses);
    }
}
