package team.router.recycle.web.route;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.router.recycle.domain.route.RouteService;
import team.router.recycle.domain.route.model.RouteRequest;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/station")
    public ResponseEntity<?> getStation() {
        return routeService.getStation();
    }

    @PostMapping("/station")
    public ResponseEntity<?> updateStation() {
        return routeService.updateStation();
    }

    @PostMapping("/cycle")
    public ResponseEntity<?> getDirection(@RequestBody RouteRequest.GetDirectionRequest getDirectionRequest) {
        return routeService.getDirection(getDirectionRequest);
    }
}
