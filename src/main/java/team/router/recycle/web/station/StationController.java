package team.router.recycle.web.station;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.station.StationService;

@RestController
@RequestMapping("/station")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/init")
    public ResponseEntity<?> initStation() {
        return stationService.initStation();
    }

    @PostMapping("/realtime")
    public ResponseEntity<?> getRealtimeStation(@RequestBody StationRequest.RealtimeStationRequest realtimeStationRequest) {
        return stationService.getRealtimeStation(realtimeStationRequest);
    }
}
