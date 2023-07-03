package team.router.recycle.domain.oauth.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OAuthClient;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

public class KakaoClient implements OAuthClient {
    
    private final WebClient tokenClient;
    private final WebClient profileClient;
    
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    
    public KakaoClient(WebClient.Builder tokenClient, WebClient.Builder profileClient) {
        this.tokenClient = tokenClient.baseUrl(authUrl).build();
        this.profileClient = profileClient.baseUrl(apiUrl).build();
    }
    
    
    @Override
    public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
        return tokenClient.post()
                .uri("/oauth/token")
                .body(oauthLoginRequest.makeBody(), LinkedMultiValueMap.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    
    @Override
    public OauthProfileResponse getOauthProfile(String accessToken) {
        return profileClient.post()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .attribute("property_keys", "[\"kakao_account.email\"]")
                .retrieve()
                .bodyToMono(KakaoMyInfo.class)
                .block();
    }
    
    @Override
    public Member.Type getMemberType() {
        return Member.Type.KAKAO;
    }
}
