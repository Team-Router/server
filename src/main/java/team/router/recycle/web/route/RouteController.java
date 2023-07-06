package team.router.recycle.web.route;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.route.RouteService;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/cycle")
    public ResponseEntity<?> getDirection(@RequestBody RouteRequest.GetDirectionRequest getDirectionRequest) {
        return routeService.getCycleDirection(getDirectionRequest);
    }

    @PostMapping("/walk")
    public ResponseEntity<?> getWalkDirection(@RequestBody RouteRequest.GetDirectionRequest getDirectionRequest) {
        return routeService.getWalkDirection(getDirectionRequest);
    }
}
