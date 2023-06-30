package team.router.recycle.web.oauth;


import feign.Body;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.oauth.OauthService;
import team.router.recycle.web.auth.TokenResponse;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OauthController {
    private final OauthService oauthService;


    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
        return ResponseEntity.ok(oauthService.login(kakaoLoginRequest));
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        return ResponseEntity.ok(oauthService.login(googleLoginRequest));
    }
}
