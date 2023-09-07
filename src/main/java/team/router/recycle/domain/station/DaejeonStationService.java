package team.router.recycle.domain.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.util.GeoUtil;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.station.StationRealtimeRequest;
import team.router.recycle.web.station.StationRealtimeResponse;
import team.router.recycle.web.station.StationsRealtimeResponse;

import java.util.*;

@Service("daejeonStationService")
@RequiredArgsConstructor
public class DaejeonStationService implements StationService {
    private final RedisTemplate<String, Station> redisTemplate;
    private final StationRepository stationRepository;
    private final DaejeonStationClient daejeonStationClient;
    private final ObjectMapper objectMapper;
    @Qualifier("stationRedisServiceImpl")
    private final StationRedisService stationRedisService;

    @Override
    public void run(ApplicationArguments args) {
        String daejeonResponse = daejeonStationClient.makeRequest();
        try {
            JsonNode daejeonJsonNode = objectMapper.readTree(daejeonResponse).get("results");
            List<Station> daejeonStationList = new ArrayList<>(daejeonJsonNode.size());
            Map<String, Station> daejeonStationMap = new HashMap<>(daejeonJsonNode.size());
            for (JsonNode node : daejeonJsonNode) {
                Station station = objectMapper.treeToValue(node, Station.class);
                if (!isDaejeon(station.getStationLatitude(), station.getStationLongitude())) {
                    continue;
                }
                daejeonStationList.add(station);
                daejeonStationMap.put(station.getStationId(), station);
            }
            stationRepository.saveAll(daejeonStationList);
            redisTemplate.opsForValue().multiSet(daejeonStationMap);
        } catch (JsonProcessingException e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "타슈 API 서버가 응답하지 않습니다.");
        }
    }

    @Override
    public StationsRealtimeResponse getRealtimeStation(StationRealtimeRequest stationRealtimeRequest) {
        double myLatitude = stationRealtimeRequest.latitude();
        double myLongitude = stationRealtimeRequest.longitude();
        double radius = 0.5;
        List<StationRealtimeResponse> stationList = new ArrayList<>();
        String daejeonResponse = daejeonStationClient.makeRequest();
        try {
            JsonNode jsonNode = objectMapper.readTree(daejeonResponse).get("results");
            for (JsonNode node : jsonNode) {
                double latitude = node.get("x_pos").asDouble();
                double longitude = node.get("y_pos").asDouble();
                if (GeoUtil.haversine(myLatitude, myLongitude, latitude, longitude) <= radius) {
                    stationList.add(objectMapper.treeToValue(node, StationRealtimeResponse.class));
                }
            }
        } catch (JsonProcessingException e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "타슈 API 서버가 응답하지 않습니다.");
        }
        return StationsRealtimeResponse.from(stationList);
    }

    @Override
    public Station findDepatureStation(Location location) {
        double myLatitude = location.latitude();
        double myLongitude = location.longitude();
        double radius = 0.5;
        List<Station> stationList = new ArrayList<>();
        String daejeonResponse = daejeonStationClient.makeRequest();
        try {
            JsonNode jsonNode = objectMapper.readTree(daejeonResponse).get("results");
            for (JsonNode node : jsonNode) {
                double latitude = node.get("x_pos").asDouble();
                double longitude = node.get("y_pos").asDouble();
                if (GeoUtil.haversine(myLatitude, myLongitude, latitude, longitude) <= radius) {
                    stationList.add(objectMapper.treeToValue(node, Station.class));
                }
            }
        } catch (JsonProcessingException e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "타슈 API 서버가 응답하지 않습니다.");
        }

        return stationList.stream()
                .filter(station -> station.getParkingBikeTotCnt() > 0)
                .min(Comparator.comparingDouble(station -> GeoUtil.haversine(myLatitude, myLongitude, station.getStationLatitude(), station.getStationLongitude())))
                .orElseThrow(() -> new RecycleException(ErrorCode.STATION_NOT_FOUND, "출발지 주변에 대여 가능한 자전거가 있는 대여소가 없습니다."));
    }

    @Override
    public Station findDestinationStation(Location location) {
        List<Station> stations = redisTemplate.opsForValue().multiGet(Objects.requireNonNull(redisTemplate.keys("*")));
        return stations.stream()
                .filter(station -> GeoUtil.haversine(location.latitude(), location.longitude(), station.getStationLatitude(), station.getStationLongitude()) <= 0.5)
                .min(Comparator.comparingDouble(station -> GeoUtil.haversine(location.latitude(), location.longitude(), station.getStationLatitude(), station.getStationLongitude())))
                .orElseThrow(() -> new RecycleException(ErrorCode.STATION_NOT_FOUND, "도착지 주변에 반납할 대여소가 없습니다."));
    }

    @Override
    public boolean isValid(String stationId) {
        return stationRedisService.isValid(stationId);
    }

    @Override
    public boolean isInvalid(String stationId) {
        return stationRedisService.isInvalid(stationId);
    }

    public boolean isDaejeon(double lat, double lng) {
        double MAX_LAT = 36.4969715; // 죽암휴게소
        double MIN_LAT = 36.1643402; // 만인산농협
        double MAX_LNG = 127.6108633; // 옥천선사공원
        double MIN_LNG = 127.2701417; // 삽재교차로

        return lat >= MIN_LAT && lat <= MAX_LAT && lng >= MIN_LNG && lng <= MAX_LNG;
    }
}
