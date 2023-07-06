package team.router.recycle.web.health;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.web.route.RouteRequest;

@RestController
@RequestMapping("/health-check")
public class healthService {
    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("서비스가 정상적으로 작동하고 있습니다.");
    }

}
