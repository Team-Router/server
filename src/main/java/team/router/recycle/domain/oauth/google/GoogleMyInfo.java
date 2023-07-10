package team.router.recycle.domain.oauth.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import team.router.recycle.domain.oauth.OauthProfileResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleMyInfo(
        @JsonProperty("email") String email
) implements OauthProfileResponse {
    @Override
    public String getEmail() {
        return email;
    }
}