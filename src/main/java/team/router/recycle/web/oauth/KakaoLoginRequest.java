package team.router.recycle.web.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import team.router.recycle.domain.member.Member;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest implements OauthLoginRequest {

    private String grantType;

    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;
    private String authorizationCode;

    @Override
    public Member.Type memberType() {
        return Member.Type.KAKAO;
    }
}