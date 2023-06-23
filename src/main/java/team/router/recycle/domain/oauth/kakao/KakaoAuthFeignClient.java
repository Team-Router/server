package team.router.recycle.domain.oauth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import team.router.recycle.web.oauth.OauthLoginRequest;

@FeignClient(name = "kakaoAuthFeignClient", url = "${oauth.kakao.url.auth}")
public interface KakaoAuthFeignClient {
    @PostMapping("/oauth/token")
    ResponseEntity<KakaoToken> getOauthAccessToken(OauthLoginRequest oauthLoginRequest);
}

