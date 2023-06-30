package team.router.recycle.domain.oauth.kakao;

import feign.Body;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import team.router.recycle.domain.oauth.AuthClient;
import team.router.recycle.web.oauth.OauthLoginRequest;

@FeignClient(name = "kakaoAuthClient", url = "${oauth.kakao.url.auth}")
@Component
public interface KakaoAuthClient extends AuthClient {
    @PostMapping("/oauth/token")
    @Headers("Content-Type: application/x-www-form-urlencoded, charset=utf-8")
    @Body("grant_type=authorization_code&client_id={clientId}&redirect_uri={redirectUri}&code={authorizationCode}")
    ResponseEntity<KakaoToken> getOauthToken(OauthLoginRequest oauthLoginRequest);
}

