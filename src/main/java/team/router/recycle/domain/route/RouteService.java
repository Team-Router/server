package team.router.recycle.domain.route;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.domain.route.RouteRequest.CycleRequest;
import team.router.recycle.domain.station.Station;
import team.router.recycle.domain.station.StationRepository;

import javax.xml.stream.Location;
import java.util.List;

@Service
public class RouteService {

    private final Response response;
    private final StationRepository stationRepository;

    public RouteService(Response response, StationRepository stationRepository) {
        this.response = response;
        this.stationRepository = stationRepository;
    }

    public ResponseEntity<?> cycleV1(CycleRequest cycleRequest) {
        double startLatitude = Double.parseDouble(cycleRequest.getStartLatitude());
        double startLongitude = Double.parseDouble(cycleRequest.getStartLongitude());
        double endLatitude = Double.parseDouble(cycleRequest.getEndLatitude());
        double endLongitude = Double.parseDouble(cycleRequest.getEndLongitude());

        double startNearDistance = Double.MAX_VALUE;
        long startNearStationId = 0;
        double endNearDistance = Double.MAX_VALUE;
        long endNearStationId = 0;

        // Todo: 다 꺼내서 비교
        List<Station> allStations = stationRepository.findAll();
        for (Station station : allStations) {
            double latitude = station.getStationLatitude();
            double longitude = station.getStationLongitude();

            double startDistance = Math.abs(startLatitude - latitude) + Math.abs(startLongitude - longitude);
            double endDistance = Math.abs(endLatitude - latitude) + Math.abs(endLongitude - longitude);

            if (startDistance < startNearDistance) {
                startNearDistance = startDistance;
                startNearStationId = station.getId();
            }
            if (endDistance < endNearDistance) {
                endNearDistance = endDistance;
                endNearStationId = station.getId();
            }
        }

        // Todo: 쿼리 날리기
        return response.success();
    }
}
