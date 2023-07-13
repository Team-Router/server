package team.router.recycle.domain.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.station.StationRealtimeRequest;
import team.router.recycle.web.station.StationRealtimeResponse;
import team.router.recycle.web.station.StationsRealtimeResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StationService {
    private final StationRepository stationRepository;
    private final ExecutorService executorService;
    private final StationClient client;
    private final ObjectMapper objectMapper;
    private static final String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};

    public StationService(StationRepository stationRepository, StationClient client, ObjectMapper objectMapper) {
        this.stationRepository = stationRepository;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @PostConstruct
    public void initStation() {
        stationRepository.truncate();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String target : TARGET_LIST) {
            List<Station> stationList = new ArrayList<>();
            futures.add(CompletableFuture.runAsync(() -> {
                        String response = client.makeRequest(target);
                        try {
                            objectMapper.readTree(response).get("rentBikeStatus").get("row").forEach(node -> {
                                try {
                                    stationList.add(objectMapper.treeToValue(node, Station.class));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        stationRepository.saveAll(stationList);
                    }
                    , executorService
            ));
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allFutures.join();
        } catch (Exception e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
        }
    }


    public Map<String, Integer> getAvailableCycle() {
        Map<String, Integer> stationMap = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String target : TARGET_LIST) {
            futures.add(CompletableFuture.runAsync(() -> {
                String response = client.makeRequest(target);
                try {
                    objectMapper.readTree(response).get("rentBikeStatus").get("row").forEach(node -> stationMap.put(node.get("stationId").asText(), node.get("parkingBikeTotCnt").asInt()));
                } catch (JsonProcessingException e) {
                    throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "따릉이 API 서버가 응답하지 않습니다.");
                }
            }, executorService));
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allFutures.thenApply(v -> stationMap).join();
    }

    @Transactional(readOnly = true)
    public StationsRealtimeResponse getRealtimeStation(StationRealtimeRequest stationRealtimeRequest) {
        double myLatitude = stationRealtimeRequest.latitude();
        double myLongitude = stationRealtimeRequest.longitude();
        double radius = 0.5;


        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<StationRealtimeResponse> stationList = new ArrayList<>();
        for (String target : TARGET_LIST) {
            futures.add(CompletableFuture.runAsync(() -> {
                        String response = client.makeRequest(target);
                        try {
                            objectMapper.readTree(response).get("rentBikeStatus").get("row").forEach(node -> {
                                try {
                                    StationRealtimeResponse station = objectMapper.treeToValue(node, StationRealtimeResponse.class);
                                    if (haversine(myLatitude, myLongitude, station.stationLatitude(), station.stationLongitude()) <= radius) {
                                        stationList.add(station);
                                    }
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    , executorService
            ));
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allFutures.join();
            return StationsRealtimeResponse.builder()
                    .count(stationList.size())
                    .stationRealtimeResponses(stationList)
                    .build();
        } catch (Exception e) {
            throw new RecycleException(ErrorCode.INTERNAL_SERVER_ERROR, "따릉이 대여소 실시간 정보 조회에 실패하였습니다.");
        }
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

    public boolean validate(String stationId) {
        return !stationRepository.existsByStationId(stationId);
    }
}
