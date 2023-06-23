package team.router.recycle.domain.oauth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OauthClient;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoogleClient implements OauthClient {
    private final GoogleAuthFeignClient googleAuthFeignClient;
    private final GoogleApiFeignClient googleApiFeignClient;

    @Override
    public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
        ResponseEntity<GoogleToken> response = googleAuthFeignClient.getOauthAccessToken(oauthLoginRequest);
        return Objects.requireNonNull(response.getBody()).getAccessToken();
    }

    @Override
    public OauthProfileResponse getOauthProfile(String accessToken) {
        String authorization = "Bearer " + accessToken;
        ResponseEntity<GoogleMyInfo> response = googleApiFeignClient.getOauthProfile(authorization);
        return response.getBody();
    }

    @Override
    public Member.Type getMemberType() {
        return Member.Type.GOOGLE;
    }
}
