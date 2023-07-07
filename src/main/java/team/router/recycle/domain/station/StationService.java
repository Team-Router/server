package team.router.recycle.domain.station;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;

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
    private final Response response;
    private final String[] TARGET_LIST = {"/1/1000", "/1001/2000", "/2001/3000"};
    
    public StationService(StationRepository stationRepository, StationClient client, ObjectMapper objectMapper, Response response) {
        this.stationRepository = stationRepository;
        this.response = response;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.objectMapper = objectMapper;
        this.client = client;
    }
    
    
    public Map<String, Integer> updateStation() {
        Map<String, Integer> stationMap = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (String target : TARGET_LIST) {
            futures.add(CompletableFuture.runAsync(() -> {
                String response = client.makeRequest(target);
                try {
                    objectMapper.readTree(response).get("rentBikeStatus").get("row").forEach(node -> stationMap.put(node.get("stationId").asText(), node.get("parkingBikeTotCnt").asInt()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
        }
        
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allFutures.thenApply(v -> stationMap).join();
    }
    
    public ResponseEntity<?> initStation() {
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
            return response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return response.fail("따릉이 대여소 DB 초기화에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    public ResponseEntity<?> getRealtimeStation() {
        // Todo: 상하좌우 위도, 경도 범위 필터
        // 예시: 내 위치가 주어졌을 때 상하좌우 3km 지점의 좌표 계산
        double myLatitude = 37.5666785;  // 내 위치의 위도
        double myLongitude = 126.9784679;  // 내 위치의 경도
        double distance = 1;  // 상하좌우로 이동할 거리 (단위: km)
        double[] coordinates = calculateCoordinatesWithinDistance(myLatitude, myLongitude, distance);
        
        double northLat = coordinates[0];
        double southLat = coordinates[1];
        double westLon = coordinates[2];
        double eastLon = coordinates[3];
        
        //
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Station> stationList = new ArrayList<>();
        loop1:
        for (String target : TARGET_LIST) {
            futures.add(CompletableFuture.runAsync(() -> {
                        String response = client.makeRequest(target);
                        try {
                            objectMapper.readTree(response).get("rentBikeStatus").get("row").forEach(node -> {
                                try {
                                    Station currecntStation = objectMapper.treeToValue(node, Station.class);
                                    // Todo: 필터에 맞는 대여소만 추가
                                    // 남 ~ 북
                                    if (currecntStation.getStationLatitude() < southLat && currecntStation.getStationLongitude() > northLat) {
                                        return;
                                    }
                                    // 서 ~ 남
                                    if (currecntStation.getStationLatitude() < westLon && currecntStation.getStationLongitude() > eastLon) {
                                        return;
                                    }
                                    stationList.add(currecntStation);
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
            System.out.println(stationList.size());
            return response.success(stationList);
        } catch (Exception e) {
            e.printStackTrace();
            return response.fail("따릉이 실시간 정보 가져오기에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
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
        double distance = radius * c;
        return distance;
    }
    
    public static double[] calculateCoordinatesWithinDistance(double latitude, double longitude, double distance) {
        // 위도와 경도를 km 단위로 변환
        double latKm = distance / 111.32;
        double lonKm = distance / (111.32 * Math.cos(Math.toRadians(latitude)));
        
        // 상하좌우 방향으로 이동한 좌표 계산
        double northLat = latitude + latKm;
        double southLat = latitude - latKm;
        double westLon = longitude - lonKm;
        double eastLon = longitude + lonKm;
        
        return new double[]{northLat, southLat, westLon, eastLon};
    }
}
