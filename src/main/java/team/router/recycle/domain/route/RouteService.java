package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;
import team.router.recycle.domain.station.StationService;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.route.GetDirectionRequest;
import team.router.recycle.web.route.GetDirectionResponse;
import team.router.recycle.web.route.GetDirectionResponseDeserializer;
import team.router.recycle.web.route.GetDirectionsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RouteService {

    private static final String WALKING_PROFILE = "walking";
    private static final String CYCLE_PROFILE = "cycling";
    private final StationRepository stationRepository;
    private final StationService stationService;
    private final RouteClient routeClient;
    private final ObjectMapper objectMapper;

    public RouteService(StationRepository stationRepository, StationService stationService, RouteClient routeClient, ObjectMapper objectMapper) {
        this.stationRepository = stationRepository;
        this.stationService = stationService;
        this.routeClient = routeClient;
        this.objectMapper = objectMapper;
        registerCustomModule();
    }

    private void registerCustomModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(GetDirectionResponse.class, new GetDirectionResponseDeserializer());
        objectMapper.registerModule(module);
    }

    public GetDirectionResponse getWalkDirection(GetDirectionRequest getDirectionRequest) {
        double startLatitude = getDirectionRequest.startLocation().latitude();
        double startLongitude = getDirectionRequest.startLocation().longitude();
        double endLatitude = getDirectionRequest.endLocation().latitude();
        double endLongitude = getDirectionRequest.endLocation().longitude();

        String coordinates = "/" + startLongitude + "," + startLatitude + ";"
                + endLongitude + "," + endLatitude;

        try {
            String result = routeClient.getRouteInfo(WALKING_PROFILE, coordinates);
            JsonNode node = objectMapper.readTree(result).get("routes").get(0);
            return objectMapper.treeToValue(node, GetDirectionResponse.class);
        } catch (IOException e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "경로 탐색 서비스가 현재 불가능합니다.");
        }
    }

    public GetDirectionsResponse getCycleDirection(GetDirectionRequest getDirectionRequest) {
        Map<String, Integer> stationsMap = stationService.getAvailableCycle();

        double startLatitude = getDirectionRequest.startLocation().latitude();
        double startLongitude = getDirectionRequest.startLocation().longitude();
        double endLatitude = getDirectionRequest.endLocation().latitude();
        double endLongitude = getDirectionRequest.endLocation().longitude();
        Station startStation = null;
        List<Station> nearestStations = stationRepository.findNearestStation(startLatitude, startLongitude, 3);
        for (Station nearestStation : nearestStations) {
            String stationId = nearestStation.getStationId();
            Integer stationBikeCnt = stationsMap.get(stationId);

            if (stationBikeCnt == null || stationBikeCnt == 0) {
                continue;
            }
            startStation = nearestStation;
            break;
        }
        if (startStation == null) {
            throw new RecycleException(ErrorCode.STATION_NOT_FOUND, "주변에 자전거가 있는 대여소가 없습니다.");
        }
        Station endStation = stationRepository.findNearestStation(endLatitude, endLongitude, 1).get(0);

        String[] PROFILE = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};
        String[] COORDINATES = {
                "/" + startLongitude + "," + startLatitude + ";"
                        + startStation.getStationLongitude() + "," + startStation.getStationLatitude(),
                "/" + startStation.getStationLongitude() + "," + startStation.getStationLatitude() + ";"
                        + endStation.getStationLongitude() + "," + endStation.getStationLatitude(),
                "/" + endStation.getStationLongitude() + "," + endStation.getStationLatitude() + ";"
                        + endLongitude + "," + endLatitude
        };

        List<GetDirectionResponse> getDirectionResponse = new ArrayList<>();

        for (int i = 0; i < PROFILE.length; i++) {
            try {
                String result = routeClient.getRouteInfo(PROFILE[i], COORDINATES[i]);
                JsonNode node = objectMapper.readTree(result).get("routes").get(0);
                getDirectionResponse.add(objectMapper.treeToValue(node, GetDirectionResponse.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return GetDirectionsResponse.builder()
                .getDirectionsResponses(getDirectionResponse)
                .build();
    }
}
