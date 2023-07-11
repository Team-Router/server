package team.router.recycle.web.oauth;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import team.router.recycle.domain.member.Member;

public record GoogleLoginRequest(String grantType, String clientId, String clientSecret, String authorizationCode,
                                 String redirectUri) implements OauthLoginRequest {

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
