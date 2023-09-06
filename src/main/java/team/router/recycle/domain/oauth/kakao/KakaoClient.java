package team.router.recycle.domain.oauth.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OAuthClient;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KakaoClient implements OAuthClient {
    @Value("${oauth.kakao.url.auth}")
    private String authUrl;

    @Value("${oauth.kakao.url.api}")
    private String apiUrl;

    private final RestClient tokenClient;
    private final RestClient infoClient;

    @Override
    public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
        return Objects.requireNonNull(tokenClient
                        .mutate()
                        .baseUrl(authUrl)
                        .build()
                        .post()
                        .uri("/oauth/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .acceptCharset(StandardCharsets.UTF_8)
                        .body(oauthLoginRequest.makeBody())
                        .retrieve()
                        .body(KakaoToken.class))
                .accessToken();
    }

    @Override
    public OauthProfileResponse getOauthProfile(String accessToken) {
        return infoClient.mutate()
                .baseUrl(apiUrl)
                .build().post()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .acceptCharset(StandardCharsets.UTF_8)
                .attribute("property_keys", "[\"kakao_account.email\"]")
                .retrieve()
                .body(KakaoMyInfo.class);
    }

    @Override
    public Member.Type getMemberType() {
        return Member.Type.KAKAO;
    }
}
