package team.router.recycle.domain.oauth.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoApiFeignClient", url = "${oauth.kakao.url.api}")
public interface KakaoApiFeignClient {
    @GetMapping("/v2/user/me")
    ResponseEntity<KakaoMyInfo> getOauthProfile(@RequestParam("property_keys") String propertyKeys,
                                                @RequestHeader("Authorization") String authorization);
}

