package team.router.recycle.web.oauth;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import team.router.recycle.domain.member.Member;

public record KakaoLoginRequest(String grantType, String clientId, String redirectUri,
                                String authorizationCode) implements OauthLoginRequest {

    @Override
    public Member.Type memberType() {
        return Member.Type.KAKAO;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);

        return UriComponentsBuilder.newInstance().queryParams(body).build().getQueryParams();
    }
}
