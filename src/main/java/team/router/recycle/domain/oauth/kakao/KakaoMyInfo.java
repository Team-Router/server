package team.router.recycle.domain.oauth.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import team.router.recycle.domain.oauth.OauthProfileResponse;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoMyInfo implements OauthProfileResponse {

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Override
    public String getEmail() {
        return kakaoAccount.getEmail();
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {
        private String email;
    }
}
