package team.router.recycle.domain.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.util.GeoUtil;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.station.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StationService implements ApplicationRunner {
    private final RedisTemplate<String, Station> redisTemplate;
    private final StationRepository stationRepository;
    private final StationClient client;
    private final DajeonStationClient dajeonStationClient;
    private final ObjectMapper objectMapper;
    private static final String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};
    
    /**
     * 따릉이 API 서버에서 대여소 정보를 받아와서 DB와 Redis에 저장
     * 타슈 API 서버에서 대여소 정보를 받아와서 DB와 Redis에 저장
     *
     * @param args incoming application arguments (unused)
     * @throws RecycleException 따릉이 API 서버가 응답하지 않을 경우
     */
    @Override
    public void run(ApplicationArguments args) {
        stationRepository.truncate();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        // seoul
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
        // dejeon
        String dajeonResponse = dajeonStationClient.makeRequest();
        try {
            JsonNode dajeonJsonNode = objectMapper.readTree(dajeonResponse).get("results");
            List<Station> dajeonStationList = new ArrayList<>(dajeonJsonNode.size());
            Map<String, Station> dajeonStationMap = new HashMap<>(dajeonJsonNode.size());
            for (JsonNode node : dajeonJsonNode) {
                Station station = jsonToStation(node);
                if (!isDajeon(station.getStationLatitude(), station.getStationLongitude())) {
                    continue;
                }
                dajeonStationList.add(station);
                dajeonStationMap.put(station.getStationId(), station);
            }
            stationRepository.saveAll(dajeonStationList);
            redisTemplate.opsForValue().multiSet(dajeonStationMap);
        } catch (JsonProcessingException e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "타슈 API 서버가 응답하지 않습니다.");
        }
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
        
        // seoul
        if (!isDajeon(myLatitude, myLongitude)) {
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
        } else {
            // dajeon
            String dajeonResponse = dajeonStationClient.makeRequest();
            try {
                JsonNode jsonNode = objectMapper.readTree(dajeonResponse).get("results");
                for (JsonNode node : jsonNode) {
                    double latitude = node.get("x_pos").asDouble();
                    double longitude = node.get("y_pos").asDouble();
                    if (GeoUtil.haversine(myLatitude, myLongitude, latitude, longitude) <= radius) {
                        stationList.add(jsonToRealtimeStation(node));
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "타슈 API 서버가 응답하지 않습니다.");
            }
        }
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
        // seoul
        if (!isDajeon(myLatitude, myLongitude)) {
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
        } else {
            // dajeon
            String dajeonResponse = dajeonStationClient.makeRequest();
            try {
                JsonNode jsonNode = objectMapper.readTree(dajeonResponse).get("results");
                for (JsonNode node : jsonNode) {
                    double latitude = node.get("x_pos").asDouble();
                    double longitude = node.get("y_pos").asDouble();
                    if (GeoUtil.haversine(myLatitude, myLongitude, latitude, longitude) <= radius) {
                        stationList.add(jsonToStation(node));
                    }
                }
            } catch (JsonProcessingException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "타슈 API 서버가 응답하지 않습니다.");
            }
        }
        
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
    
    /**
     * @param node 타슈 API 서버에서 받아온 대여소 정보
     * @return Station
     */
    public Station jsonToStation(JsonNode node) {
        String stationId = node.get("id").asText().replace("ST", "DJ-");
        return Station.builder()
                .stationName(node.get("name").asText())
                .stationId(stationId)
                .stationLatitude(node.get("x_pos").asDouble())
                .stationLongitude(node.get("y_pos").asDouble())
                .parkingBikeTotCnt(node.get("parking_count").asInt())
                .build();
    }
    
    /**
     * @param node 타슈 API 서버에서 받아온 대여소 정보
     * @return StationRealtimeResponse
     */
    public StationRealtimeResponse jsonToRealtimeStation(JsonNode node) {
        String stationId = node.get("id").asText().replace("ST", "DJ-");
        return StationRealtimeResponse.builder()
                .stationName(node.get("name").asText())
                .stationId(stationId)
                .stationLatitude(node.get("x_pos").asDouble())
                .stationLongitude(node.get("y_pos").asDouble())
                .parkingBikeTotCnt(node.get("parking_count").asInt())
                .build();
    }
    
    /**
     * @param lat 위도
     * @param lng 경도
     * @return 대전 내에 있는지 여부
     */
    public boolean isDajeon(double lat, double lng) {
        double MAX_LAT = 36.4969715; // 죽암휴게소
        double MIN_LAT = 36.1643402; // 만인산농협
        double MAX_LNG = 127.6108633; // 옥천선사공원
        double MIN_LNG = 127.2701417; // 삽재교차로
        
        return lat >= MIN_LAT && lat <= MAX_LAT && lng >= MIN_LNG && lng <= MAX_LNG;
    }
}
