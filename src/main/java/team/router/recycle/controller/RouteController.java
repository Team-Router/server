package team.router.recycle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.router.recycle.domain.route.RouteRequest.CycleRequest;
import team.router.recycle.domain.route.RouteService;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/route")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/cycle")
    public ResponseEntity<?> cycleV1(@RequestBody CycleRequest cycleRequest) throws MalformedURLException {
        return routeService.cycleV1(cycleRequest);
    }

    @PostMapping("/station")
    public ResponseEntity<?> updateStation() throws MalformedURLException {
        return routeService.updateStation();
    }

    @GetMapping("/station")
    public ResponseEntity<?> getStation() {
        return routeService.getStation();
    }
}
