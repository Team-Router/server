package team.router.recycle.web.oauth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import team.router.recycle.domain.member.Member;

@Getter
@NoArgsConstructor
public class KakaoLoginRequest implements OauthLoginRequest {
    
    private String grantType = "authorization_code";
    
    //    @Value("${oauth.kakao.client-id}")
    private String clientId = "535bf37029bd14fca9a5e7b3c0d4f283";
    
    //    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri = "http://localhost:8080/login/oauth2/code/kakao";
    
    private String authorizationCode = "9rU-pJEFrWLEMSm92s4XWtUT-EnvAs1tblOLGumhyKA__iVHWfPEEwSoKYCCKpD4v6Dr9Ao9dRkAAAGJGuZ_qg";
    
    @Override
    public Member.Type memberType() {
        return Member.Type.KAKAO;
    }
    
    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("redirectUri", redirectUri);
        body.add("code", authorizationCode);
        
        return body;
    }
}
