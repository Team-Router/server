package team.router.recycle.domain.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.domain.station.Station;
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

@Service
public class RouteService {

    private static final String WALKING_PROFILE = "walking/";
    private static final String CYCLE_PROFILE = "cycling/";
    private final StationService stationService;
    private final RouteClient routeClient;
    private final ObjectMapper objectMapper;

    public RouteService(StationService stationService, RouteClient routeClient, ObjectMapper objectMapper) {
        this.stationService = stationService;
        this.routeClient = routeClient;
        this.objectMapper = objectMapper.registerModule(new SimpleModule().addDeserializer(GetDirectionResponse.class, new GetDirectionResponseDeserializer()));
    }

    @Transactional(readOnly = true)
    public GetDirectionResponse getWalkDirection(GetDirectionRequest getDirectionRequest) {
        if (getDirectionRequest.isInvalidWalkRequest(getDirectionRequest)) {
            return GetDirectionResponse.EMPTY;
        }
        String coordinates = getCoordinates(getDirectionRequest.startLocation().toString(), getDirectionRequest.endLocation().toString());

        try {
            return newGetDirectionResponse(WALKING_PROFILE, coordinates);
        } catch (IOException e) {
            throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "경로 탐색 서비스가 현재 불가능합니다.");
        }
    }

    @Transactional(readOnly = true)
    public GetDirectionsResponse getCycleDirection(GetDirectionRequest getDirectionRequest) {
        if (getDirectionRequest.isInvalidCycleRequest(getDirectionRequest)) {
            return GetDirectionsResponse.EMPTY;
        }
        Location startLocation = getDirectionRequest.startLocation();
        Location endLocation = getDirectionRequest.endLocation();

        Station startStation = stationService.findStartStation(startLocation);
        Station endStation = stationService.findNearestStation(endLocation);

        String[] profiles = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};
        String[] coordinates = {
                getCoordinates(startLocation.toString(), startStation.toLocationString()),
                getCoordinates(startStation.toLocationString(), endStation.toLocationString()),
                getCoordinates(endStation.toLocationString(), endLocation.toString())
        };

        List<GetDirectionResponse> getDirectionResponses = new ArrayList<>();

        for (int i = 0; i < profiles.length; i++) {
            try {
                getDirectionResponses.add(newGetDirectionResponse(profiles[i], coordinates[i]));
            } catch (IOException e) {
                throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "경로 탐색 서비스가 현재 불가능합니다.");
            }
        }

        return GetDirectionsResponse.builder()
                .getDirectionsResponses(getDirectionResponses)
                .build();
    }

    private static String getCoordinates(String from, String to) {
        return from + ";" + to;
    }

    private GetDirectionResponse newGetDirectionResponse(String profile, String coordinate) throws JsonProcessingException {
        return objectMapper.treeToValue(getRoutes(profile, coordinate), GetDirectionResponse.class);
    }

    private JsonNode getRoutes(String profile, String coordinate) throws JsonProcessingException {
        return objectMapper.readTree(routeClient.getRouteInfo(profile, coordinate)).get("routes").get(0);
    }
}
