package team.router.recycle.domain.oauth.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OauthClient;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

import java.util.Objects;

@Component
@EnableFeignClients
@RequiredArgsConstructor
public class KakaoClient implements OauthClient {
    private final KakaoAuthFeignClient kakaoAuthFeignClient;
    private final KakaoApiFeignClient kakaoApiFeignClient;

    @Override
    public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
        ResponseEntity<KakaoToken> response = kakaoAuthFeignClient.getOauthAccessToken(oauthLoginRequest);
        return Objects.requireNonNull(response.getBody()).getAccessToken();
    }

    @Override
    public OauthProfileResponse getOauthProfile(String accessToken) {
        String propertyKeys = "[\"kakao_account.email\", \"kakao_account.profile\"]";
        String authorization = "Bearer " + accessToken;
        ResponseEntity<KakaoMyInfo> response = kakaoApiFeignClient.getOauthProfile(propertyKeys, authorization);
        return response.getBody();
    }

    @Override
    public Member.Type getMemberType() {
        return Member.Type.KAKAO;
    }
}
