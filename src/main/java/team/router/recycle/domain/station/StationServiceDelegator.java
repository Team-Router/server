package team.router.recycle.domain.station;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;
import team.router.recycle.web.route.GetDirectionRequest;
import team.router.recycle.web.station.StationRealtimeRequest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StationServiceDelegator {
    private final Map<City, StationService> services;

    @Autowired
    public StationServiceDelegator(List<StationService> stationServices) {
        this.services = stationServices.stream()
                .collect(Collectors.toMap(StationService::handledCity, Function.identity()));
    }

    public StationService getStationServiceForLocation(Double latitude, Double longitude) {
        City city = CityDeterminer.determineCity(latitude, longitude);
        StationService service = services.get(city);

        if (service == null) {
            throw new RecycleException(ErrorCode.BAD_REQUEST, "해당 지역의 대여소 정보를 찾을 수 없습니다.");
        }

        return service;
    }

    public StationService getStationServiceForRealtimeRequest(StationRealtimeRequest request) {
        return this.getStationServiceForLocation(request.latitude(), request.longitude());
    }

    public StationService getStationServiceForGetDirectionRequest(GetDirectionRequest request) {
        Location startLocation = request.startLocation();
        Location endLocation = request.endLocation();

        if (!CityDeterminer.isSameCity(startLocation, endLocation)) {
            throw new RecycleException(ErrorCode.BAD_REQUEST, "출발지와 도착지가 같은 지역이 아닙니다.");
        }

        return this.getStationServiceForLocation(startLocation.latitude(), startLocation.longitude());
    }
}
