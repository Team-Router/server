package team.router.recycle.domain.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import team.router.recycle.domain.oauth.OauthProfileResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoMyInfo(
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) implements OauthProfileResponse {
    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Getter
    static class KakaoAccount {
        private String email;
    }
}
