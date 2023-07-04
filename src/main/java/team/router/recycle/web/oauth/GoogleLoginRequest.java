package team.router.recycle.web.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import team.router.recycle.domain.member.Member;

@Getter
@NoArgsConstructor
public class GoogleLoginRequest implements OauthLoginRequest {

    private String grantType;

    private String clientId;

    private String clientSecret;

    private String authorizationCode;

    private String redirectUri;

    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", authorizationCode);
        body.add("redirect_uri", redirectUri);

        return body;
    }

    @Override
    public Member.Type memberType() {
        return Member.Type.GOOGLE;
    }
}