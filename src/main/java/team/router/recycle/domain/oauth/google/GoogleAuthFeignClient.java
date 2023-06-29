package team.router.recycle.domain.oauth.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import team.router.recycle.web.oauth.OauthLoginRequest;

@FeignClient(name = "googleAuthFeignClient", url = "${oauth.google.url.auth}")
public interface GoogleAuthFeignClient {
    @PostMapping("/token")
    ResponseEntity<GoogleToken> getOauthAccessToken(OauthLoginRequest oauthLoginRequest);
}
