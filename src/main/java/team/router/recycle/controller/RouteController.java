package team.router.recycle.controller;

import org.springframework.http.ResponseEntity;
import team.router.recycle.domain.route.RouteRequest.CycleRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
