package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.route.RouteRequest.CycleRequest;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class RouteService {

    private final Response response;
    private final StationRepository stationRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public RouteService(Response response, StationRepository stationRepository, RedisTemplate<String, String> redisTemplate) {
        this.response = response;
        this.stationRepository = stationRepository;
        this.redisTemplate = redisTemplate;
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

    public ResponseEntity<?> updateStation() throws MalformedURLException {
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        String API_KEY = "/5473736b67687975343450566e4455";
        String BIKE_PATH = "/json/bikeList";
        String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        for (String target : TARGET_LIST) {
            URL MASTER_URL = new URL(BASE_URL + API_KEY + BIKE_PATH + target);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8));
                String result = br.readLine();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result).get("rentBikeStatus").get("row");
                for (JsonNode station : jsonNode) {
                    String stationId = station.get("stationId").asText();
                    String parkingBikeTotCnt = station.get("parkingBikeTotCnt").asText();
                    valueOperations.set(stationId, parkingBikeTotCnt);
                }
            } catch (IOException e) {
                return response.fail(String.valueOf(e), HttpStatus.BAD_REQUEST);
            }
        }
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
                "/" + startLongitude + "," + startLatitude + ";" + startStation.getStationLongitude() + "," + startStation.getStationLatitude(),
                "/" + startStation.getStationLongitude() + "," + startStation.getStationLatitude() + ";" + endStation.getStationLongitude() + "," + endStation.getStationLatitude(),
                "/" + endStation.getStationLongitude() + "," + endStation.getStationLatitude() + ";" + endLongitude + "," + endLatitude
        };
        String ACCESS_TOKEN = "&access_token=pk.eyJ1IjoiaHl1bnNlb2tjaGVvbmciLCJhIjoiY2xpZHZvc3ptMDIweDNqbzA4b2ljeGhjMiJ9.0iw5JNcWKW4cbFAezMrHSw";

        for (int i = 0; i < 3; i++) {
            URL MASTER_URL = new URL(BASE_URL + PROFILE[i] + COORDINATES[i] + GEOJSON + ACCESS_TOKEN + LANGUAGE + STEPS);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(MASTER_URL.openStream(), StandardCharsets.UTF_8));
                String result = br.readLine();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(result).get("routes");
                JsonNode jsonNode1 = objectMapper.readTree(result).get("waypoints");
                for (JsonNode node : jsonNode1) {
                    System.out.println(node.get("location").toString());
                }

            } catch (Exception e) {
                return response.fail(String.valueOf(e), HttpStatus.BAD_REQUEST);
            }
        }
        return response.success();
    }
}
