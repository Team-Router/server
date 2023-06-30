package team.router.recycle.domain.oauth.kakao;

import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import team.router.recycle.domain.oauth.InfoClient;
import team.router.recycle.domain.oauth.OauthProfileResponse;

@FeignClient(name = "kakaoInfoClient", url = "${oauth.kakao.url.api}")
@Component
public interface KakaoInfoClient extends InfoClient {
    @Override
    @GetMapping("/v2/user/me")
    @Headers({"Authorization: Bearer {accessToken}", "Content-Type: application/x-www-form-urlencoded, charset=UTF-8"})
    // property_keys의 kakao_account.email을 가져온다.
    @Param("property_keys: [\"kakao_account.email\"]")
    OauthProfileResponse getOauthProfile(String accessToken);
}

