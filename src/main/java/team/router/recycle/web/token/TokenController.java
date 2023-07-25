package team.router.recycle.web.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    @RequestMapping("")
    public ResponseEntity<?> validateToken() {
        return ResponseEntity.ok().build();
    }
}
