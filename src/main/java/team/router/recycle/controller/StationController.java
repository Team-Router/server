package team.router.recycle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.Response;
import team.router.recycle.domain.station.StationService;

@RestController
@RequestMapping("/station")
public class StationController {

    private final StationService stationService;
    private final Response response;

    public StationController(StationService stationService, Response response) {
        this.stationService = stationService;
        this.response = response;
    }

    @PostMapping("/init")
    public ResponseEntity<?> initStation() {
        stationService.initStation();
        return response.success();
    }
}
