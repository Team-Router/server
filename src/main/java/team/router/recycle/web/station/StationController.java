package team.router.recycle.web.station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.station.StationServiceFactory;

@RestController
@RequestMapping("/station")
@RequiredArgsConstructor
public class StationController {

    private final StationServiceFactory stationServiceFactory;

    @PostMapping("/realtime")
    public ResponseEntity<?> getRealtimeStation(@RequestBody StationRealtimeRequest stationRealtimeRequest) {
        return ResponseEntity.ok(stationServiceFactory.getStationService(stationRealtimeRequest).getRealtimeStation(stationRealtimeRequest));
    }
}
