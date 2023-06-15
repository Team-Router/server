package team.router.recycle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.router.recycle.domain.route.RouteRequest.getDirectionRequest;
import team.router.recycle.domain.route.RouteService;

@RestController
@RequestMapping("/route")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/cycle")
    public ResponseEntity<?> cycleV1(@RequestBody getDirectionRequest getDirectionRequest) {
        return routeService.cycleV1(getDirectionRequest);
    }

    @PostMapping("/station")
    public ResponseEntity<?> updateStation() {
        return routeService.updateStation();
    }

    @GetMapping("/station")
    public ResponseEntity<?> getStation() {
        return routeService.getStation();
    }
}
