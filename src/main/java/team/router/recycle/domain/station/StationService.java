package team.router.recycle.domain.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.station.StationRealtimeRequest;
import team.router.recycle.web.station.StationRealtimeResponse;
import team.router.recycle.web.station.StationsRealtimeResponse;

import java.util.*;

@Service
public class StationService implements ApplicationRunner {
    private final StationRepository stationRepository;
    private final StationClient client;
    private final ObjectMapper objectMapper;
    private static final String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};

    public StationService(StationRepository stationRepository, StationClient client, ObjectMapper objectMapper) {
        this.stationRepository = stationRepository;
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        stationRepository.truncate();
        Arrays.stream(TARGET_LIST).parallel().forEach(target -> {
            String response = client.makeRequest(target);
            try {
                JsonNode jsonNode = objectMapper.readTree(response).get("rentBikeStatus").get("row");
                List<Station> stationList = new ArrayList<>(jsonNode.size());
                for (JsonNode node : jsonNode) {
                    stationList.add(objectMapper.treeToValue(node, Station.class));
                }
                stationRepository.saveAll(stationList);
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
            }
        });
    }

    @Transactional(readOnly = true)
    public StationsRealtimeResponse getRealtimeStation(StationRealtimeRequest stationRealtimeRequest) {
        double myLatitude = stationRealtimeRequest.latitude();
        double myLongitude = stationRealtimeRequest.longitude();
        double radius = 0.5;
        List<StationRealtimeResponse> stationList = new ArrayList<>();

        Arrays.stream(TARGET_LIST).parallel().forEach(target -> {
            String response = client.makeRequest(target);
            try {
                JsonNode jsonNode = objectMapper.readTree(response).get("rentBikeStatus").get("row");
                for (JsonNode node : jsonNode) {
                    StationRealtimeResponse station = objectMapper.treeToValue(node, StationRealtimeResponse.class);
                    if (haversine(myLatitude, myLongitude, station.stationLatitude(), station.stationLongitude()) <= radius) {
                        stationList.add(station);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
            }
        });
        return StationsRealtimeResponse.builder()
                .count(stationList.size())
                .stationRealtimeResponses(stationList)
                .build();
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // 지구의 반지름 (단위: km)
        double radius = 6371;

        // 위도와 경도를 라디안으로 변환
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // 위도와 경도의 차이 계산
        double dlat = lat2Rad - lat1Rad;
        double dlon = lon2Rad - lon1Rad;

        // Haversine 공식을 사용하여 거리 계산
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리를 km 단위로 반환
        return radius * c;
    }

    public Station findStartStation(Location location) {
        Map<String, Integer> availableCycle = getAvailableCycle();
        List<Station> nearestStations = findNearestStation(location, 3);
        return nearestStations.stream()
                .filter(station -> availableCycle.get(station.getStationId()) > 0)
                .findFirst()
                .orElseThrow(() -> new RecycleException(ErrorCode.STATION_NOT_FOUND, "주변에 자전거가 있는 대여소가 없습니다."));
    }

    public Map<String, Integer> getAvailableCycle() {
        Map<String, Integer> stationMap = new HashMap<>();
        Arrays.stream(TARGET_LIST).parallel().forEach(target -> {
            String response = client.makeRequest(target);
            try {
                objectMapper.readTree(response).get("rentBikeStatus").get("row")
                        .forEach(node -> stationMap.put(node.get("stationId").asText(), node.get("parkingBikeTotCnt").asInt()));
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
            }
        });
        return stationMap;
    }

    public boolean validate(String stationId) {
        return !stationRepository.existsByStationId(stationId);
    }

    public List<Station> findNearestStation(Location location, int count) {
        return findNearestStation(location.latitude(), location.longitude(), count);
    }

    private List<Station> findNearestStation(double latitude, double longitude, int count) {
        return stationRepository.findNearestStations(latitude, longitude, count);
    }

    public Station findNearestStation(Location location) {
        return findNearestStation(location.latitude(), location.longitude());
    }

    private Station findNearestStation(double latitude, double longitude) {
        return stationRepository.findNearestStations(latitude, longitude);
    }
}
