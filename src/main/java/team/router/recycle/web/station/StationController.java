package team.router.recycle.web.station;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.station.StationService;

@RestController
@RequestMapping("/station")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping("/realtime")
    public ResponseEntity<?> getRealtimeStation(@RequestBody StationRealtimeRequest stationRealtimeRequest) {
        return ResponseEntity.ok(stationService.getRealtimeStation(stationRealtimeRequest));
    }
}
