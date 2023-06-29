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

    @PostMapping("/cycle")
    public ResponseEntity<?> getDirection(@RequestBody RouteRequest.GetDirectionRequest getDirectionRequest) {
        return routeService.getDirection(getDirectionRequest);
    }
}
