package team.router.recycle.domain.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.web.route.GetDirectionResponseDeserializer;
import team.router.recycle.web.route.RouteRequest;
import team.router.recycle.web.route.RouteResponse;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;
import team.router.recycle.domain.station.StationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RouteService {

    private static final String WALKING_PROFILE = "walking";
    private static final String CYCLE_PROFILE = "cycling";
    private final Response response;
    private final StationRepository stationRepository;
    private final StationService stationService;
    private final RouteClient routeClient;
    private final ObjectMapper objectMapper;

    public RouteService(Response response, StationRepository stationRepository, StationService stationService, RouteClient routeClient, ObjectMapper objectMapper) {
        this.response = response;
        this.stationRepository = stationRepository;
        this.stationService = stationService;
        this.routeClient = routeClient;
        this.objectMapper = objectMapper;
        registerCustomModule();
    }

    private void registerCustomModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(RouteResponse.getDirectionResponse.class, new GetDirectionResponseDeserializer());
        objectMapper.registerModule(module);
    }

    public ResponseEntity<?> getWalkDirection(RouteRequest.GetDirectionRequest getDirectionRequest) {
        double startLatitude = getDirectionRequest.getStartLocation().latitude();
        double startLongitude = getDirectionRequest.getStartLocation().longitude();
        double endLatitude = getDirectionRequest.getEndLocation().latitude();
        double endLongitude = getDirectionRequest.getEndLocation().longitude();

        RouteResponse.getDirectionResponse getDirectionResponse = new RouteResponse.getDirectionResponse();
        String coordinates = "/" + startLongitude + "," + startLatitude + ";"
                + endLongitude + "," + endLatitude;

        try {
            String result = routeClient.getRouteInfo(WALKING_PROFILE, coordinates);
            JsonNode node = objectMapper.readTree(result).get("routes").get(0);
            getDirectionResponse = objectMapper.treeToValue(node, RouteResponse.getDirectionResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.success(getDirectionResponse);
    }

    public ResponseEntity<?> getCycleDirection(RouteRequest.GetDirectionRequest getDirectionRequest) {
        Map<String, Integer> stationsMap = stationService.updateStation();
        if (stationsMap.isEmpty()) {
            return response.success("현재 대여 가능한 자전거가 없습니다.");
        }

        double startLatitude = getDirectionRequest.getStartLocation().latitude();
        double startLongitude = getDirectionRequest.getStartLocation().longitude();
        double endLatitude = getDirectionRequest.getEndLocation().latitude();
        double endLongitude = getDirectionRequest.getEndLocation().longitude();
        Station startStation = null;
        List<Station> nearestStations = stationRepository.findNearestStation(startLatitude, startLongitude, 3);
        for (Station nearestStation : nearestStations) {
            String stationId = nearestStation.getStationId();
            Integer stationBikeCnt = stationsMap.get(stationId);

            if (stationBikeCnt == null || stationBikeCnt == 0) {
                System.out.println(stationId);
                continue;
            }
            startStation = nearestStation;
            break;
        }
        if (startStation == null) {
            return response.success("현재 대여 가능한 자전거가 없습니다.");
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

        List<RouteResponse.getDirectionResponse> getDirectionResponses = new ArrayList<>();

        for (int i = 0; i < PROFILE.length; i++) {
            try {
                String result = routeClient.getRouteInfo(PROFILE[i], COORDINATES[i]);
                JsonNode node = objectMapper.readTree(result).get("routes").get(0);
                getDirectionResponses.add(objectMapper.treeToValue(node, RouteResponse.getDirectionResponse.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response.success(getDirectionResponses);
    }
}
