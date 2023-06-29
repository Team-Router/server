package team.router.recycle.domain.oauth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OauthClient;
import team.router.recycle.domain.oauth.OauthInfo;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.GoogleLoginRequest;

@Service
@RequiredArgsConstructor
public class GoogleOauthService {
    private final OauthClient googleClient;

    public OauthInfo getGoogleInfo(GoogleLoginRequest googleLoginRequest) {
        String accessToken = googleClient.getOauthAccessToken(googleLoginRequest);
        OauthProfileResponse oauthProfile = googleClient.getOauthProfile(accessToken);

        return OauthInfo.builder()
                .email(oauthProfile.getEmail())
                .type(Member.Type.GOOGLE)
                .build();
    }
}
