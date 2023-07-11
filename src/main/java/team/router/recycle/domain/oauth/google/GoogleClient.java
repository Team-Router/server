package team.router.recycle.domain.oauth.google;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OAuthClient;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GoogleClient implements OAuthClient {
    
    @Value("${oauth.google.url.auth}")
    private String authUrl;
    
    @Value("${oauth.google.url.api}")
    private String apiUrl;
    
    private final WebClient tokenClient;
    private final WebClient infoClient;
    
    @Override
    public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
        return Objects.requireNonNull(tokenClient
                        .mutate()
                        .baseUrl(authUrl)
                        .build()
                        .post()
                        .uri("/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .acceptCharset(StandardCharsets.UTF_8)
                        .bodyValue(oauthLoginRequest.makeBody())
                        .retrieve()
                        .bodyToMono(GoogleToken.class)
                        .block())
                .accessToken();
    }
    
    @Override
    public OauthProfileResponse getOauthProfile(String accessToken) {
        return infoClient.mutate().baseUrl(apiUrl)
                .build()
                .get()
                .uri("/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
//                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(GoogleMyInfo.class)
                .block();
    }
    
    @Override
    public Member.Type getMemberType() {
        return Member.Type.GOOGLE;
    }
}
