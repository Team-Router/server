package team.router.recycle.web.route;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.route.RouteService;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/cycle")
    public ResponseEntity<?> getDirection(@RequestBody GetDirectionRequest getDirectionRequest) {
        return ResponseEntity.ok(routeService.getCycleDirection(getDirectionRequest));
    }

    @PostMapping("/walk")
    public ResponseEntity<?> getWalkDirection(@RequestBody GetDirectionRequest getDirectionRequest) {
        return ResponseEntity.ok(routeService.getWalkDirection(getDirectionRequest));
    }
}
