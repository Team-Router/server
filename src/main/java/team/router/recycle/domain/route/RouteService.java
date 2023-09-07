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
import team.router.recycle.domain.station.StationServiceDelegator;
import team.router.recycle.util.GeoUtil;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.route.GetDirectionRequest;
import team.router.recycle.web.route.GetDirectionResponse;
import team.router.recycle.web.route.GetDirectionResponseDeserializer;
import team.router.recycle.web.route.GetDirectionsResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class RouteService {

    private static final String WALKING_PROFILE = "walking/";
    private static final String CYCLE_PROFILE = "cycling/";
    private final StationServiceDelegator stationServiceDelegator;
    private final RouteClient routeClient;
    private final ObjectMapper objectMapper;

    public RouteService(StationServiceDelegator stationServiceDelegator, RouteClient routeClient, ObjectMapper objectMapper) {
        this.stationServiceDelegator = stationServiceDelegator;
        this.routeClient = routeClient;
        this.objectMapper = objectMapper.registerModule(new SimpleModule().addDeserializer(GetDirectionResponse.class, new GetDirectionResponseDeserializer()));
    }

    @Transactional(readOnly = true)
    public GetDirectionResponse getWalkDirection(GetDirectionRequest getDirectionRequest) {
        if (isInvalidWalkRequest(getDirectionRequest)) {
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
        if (isInvalidCycleRequest(getDirectionRequest)) {
            return GetDirectionsResponse.EMPTY;
        }
        Location startLocation = getDirectionRequest.startLocation();
        Location endLocation = getDirectionRequest.endLocation();

        StationService stationService = stationServiceDelegator.getStationServiceForGetDirectionRequest(getDirectionRequest);

        Station depatureStation = stationService.findDepatureStation(startLocation);
        Station destinationStation = stationService.findDestinationStation(endLocation);

        String[] profiles = {WALKING_PROFILE, CYCLE_PROFILE, WALKING_PROFILE};
        String[] coordinates = {
                getCoordinates(startLocation.toString(), depatureStation.toLocationString()),
                getCoordinates(depatureStation.toLocationString(), destinationStation.toLocationString()),
                getCoordinates(destinationStation.toLocationString(), endLocation.toString())
        };

        List<GetDirectionResponse> getDirectionResponses = new ArrayList<>();
        IntStream.range(0, profiles.length)
                .parallel()
                .forEach(i -> {
                    try {
                        getDirectionResponses.add(newGetDirectionResponse(profiles[i], coordinates[i]));
                    } catch (IOException e) {
                        throw new RecycleException(ErrorCode.SERVICE_UNAVAILABLE, "경로 탐색 서비스가 현재 불가능합니다.");
                    }
                });

        return GetDirectionsResponse.from(getDirectionResponses);
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

    public boolean isInvalidCycleRequest(GetDirectionRequest request) {
        return GeoUtil.haversine(request.startLocation().latitude(), request.startLocation().longitude(),
                request.endLocation().latitude(), request.endLocation().longitude()) < 0.5;
    }

    public boolean isInvalidWalkRequest(GetDirectionRequest request) {
        return GeoUtil.haversine(request.startLocation().latitude(), request.startLocation().longitude(),
                request.endLocation().latitude(), request.endLocation().longitude()) > 30;
    }
}
