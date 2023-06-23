package team.router.recycle.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.router.recycle.domain.oauth.OauthClient;
import team.router.recycle.domain.oauth.google.GoogleClient;
import team.router.recycle.domain.oauth.kakao.KakaoClient;

@Configuration
@EnableFeignClients(basePackages = "team.router.recycle.domain.oauth")
public class ClientConfig {

    @Bean
    public OauthClient kakaoClient(KakaoClient kakaoClient) {
        return kakaoClient;
    }

    @Bean
    public OauthClient googleClient(GoogleClient googleClient) {
        return googleClient;
    }
}
