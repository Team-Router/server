package team.router.recycle.web.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import team.router.recycle.domain.member.Member;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest implements OauthLoginRequest {

    private String grantType;
    private String clientId;
    private String redirectUri;
    private String authorizationCode;

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

        return UriComponentsBuilder.newInstance()
                .queryParams(body)
                .build()
                .getQueryParams();
    }
}
