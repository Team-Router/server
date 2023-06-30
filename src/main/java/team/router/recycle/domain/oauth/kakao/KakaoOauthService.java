package team.router.recycle.domain.oauth.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.AuthClient;
import team.router.recycle.domain.oauth.InfoClient;
import team.router.recycle.domain.oauth.OauthInfo;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

@Service
@RequiredArgsConstructor
public class KakaoOauthService {
    private final InfoClient kakaoInfoClient;
    private final AuthClient kakaoAuthClient;

    public OauthInfo getKakaoInfo(OauthLoginRequest oauthLoginRequest) {
        String accessToken = kakaoAuthClient.getOauthAccessToken(oauthLoginRequest);
        OauthProfileResponse oauthProfile = kakaoInfoClient.getOauthProfile(accessToken);

        return OauthInfo.builder()
                .email(oauthProfile.getEmail())
                .type(Member.Type.KAKAO)
                .build();
    }
}
