package team.router.recycle.domain.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.util.GeoUtil;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.station.StationRealtimeRequest;
import team.router.recycle.web.station.StationRealtimeResponse;
import team.router.recycle.web.station.StationsRealtimeResponse;

import java.util.*;

@Service
public class StationService implements ApplicationRunner {
    private final RedisTemplate<String, Station> redisTemplate;
    private final StationRepository stationRepository;
    private final StationClient client;
    private final ObjectMapper objectMapper;
    private static final String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};

    public StationService(RedisTemplate<String, Station> redisTemplate, StationRepository stationRepository, StationClient client, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.stationRepository = stationRepository;
        this.objectMapper = objectMapper;
        this.client = client;
    }

    /**
     * @param args incoming application arguments (unused)
     * @throws RecycleException 따릉이 API 서버가 응답하지 않을 경우
     *                          <p>
     *                          따릉이 API 서버에서 대여소 정보를 받아와서 Redis에 저장
     */
    @Override
    public void run(ApplicationArguments args) {
        stationRepository.truncate();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        Arrays.stream(TARGET_LIST).parallel().forEach(target -> {
            String response = client.makeRequest(target);
            try {
                JsonNode jsonNode = objectMapper.readTree(response).get("rentBikeStatus").get("row");
                List<Station> stationList = new ArrayList<>(jsonNode.size());
                Map<String, Station> stationMap = new HashMap<>(jsonNode.size());
                for (JsonNode node : jsonNode) {
                    Station station = objectMapper.treeToValue(node, Station.class);
                    stationList.add(station);
                    stationMap.put(node.get("stationId").asText(), station);
                }
                stationRepository.saveAll(stationList);
                redisTemplate.opsForValue().multiSet(stationMap);
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
            }
        });
    }

    /**
     * @param stationRealtimeRequest 사용자의 현재 위치
     * @return 사용자의 현재 위치에서 반경 500m 이내에 있는 대여소 정보
     * @throws RecycleException 따릉이 API 서버가 응답하지 않을 경우
     */
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
                    if (GeoUtil.haversine(myLatitude, myLongitude, node.get("stationLatitude").asDouble(), node.get("stationLongitude").asDouble()) <= radius) {
                        stationList.add(objectMapper.treeToValue(node, StationRealtimeResponse.class));
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

    /**
     * @param location 위치 정보
     * @return 위치에서 반경 500m 이내에 있는 대여 가능한 대여소 중 가장 가까운 대여소 정보
     * @throws RecycleException 따릉이 API 서버가 응답하지 않을 경우
     * @throws RecycleException 반경 500m 이내에 대여 가능한 대여소가 없을 경우
     */
    public Station findDepatureStation(Location location) {
        double myLatitude = location.latitude();
        double myLongitude = location.longitude();
        double radius = 0.5;
        List<Station> stationList = new ArrayList<>();

        Arrays.stream(TARGET_LIST).parallel().forEach(target -> {
            String response = client.makeRequest(target);
            try {
                JsonNode jsonNode = objectMapper.readTree(response).get("rentBikeStatus").get("row");
                for (JsonNode node : jsonNode) {
                    if (GeoUtil.haversine(myLatitude, myLongitude, node.get("stationLatitude").asDouble(), node.get("stationLongitude").asDouble()) <= radius) {
                        stationList.add(objectMapper.treeToValue(node, Station.class));
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
            }
        });
        return stationList.stream()
                .filter(station -> station.getParkingBikeTotCnt() > 0)
                .min(Comparator.comparingDouble(station -> GeoUtil.haversine(myLatitude, myLongitude, station.getStationLatitude(), station.getStationLongitude())))
                .orElseThrow(() -> new RecycleException(ErrorCode.STATION_NOT_FOUND, "출발지 주변에 대여 가능한 자전거가 있는 대여소가 없습니다."));
    }

    /**
     * @param location 위치 정보
     * @return 위치에서 반경 500m 이내에 있는 반납 가능한 대여소 중 가장 가까운 대여소 정보
     * @throws RecycleException 반경 500m 이내에 반납 가능한 대여소가 없을 경우
     */
    public Station findDestinationStation(Location location) {
        List<Station> stations = redisTemplate.opsForValue().multiGet(Objects.requireNonNull(redisTemplate.keys("*")));
        return stations.stream()
                .filter(station -> GeoUtil.haversine(location.latitude(), location.longitude(), station.getStationLatitude(), station.getStationLongitude()) <= 0.5)
                .min(Comparator.comparingDouble(station -> GeoUtil.haversine(location.latitude(), location.longitude(), station.getStationLatitude(), station.getStationLongitude())))
                .orElseThrow(() -> new RecycleException(ErrorCode.STATION_NOT_FOUND, "도착지 주변에 반납할 대여소가 없습니다."));
    }

    public boolean isValid(String stationId) {
        return redisTemplate.hasKey(stationId);
    }

    public boolean isInvalid(String stationId) {
        return !isValid(stationId);
    }

}
