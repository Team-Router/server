package team.router.recycle.domain.oauth.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import team.router.recycle.domain.oauth.OauthProfileResponse;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMyInfo implements OauthProfileResponse {
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty("email")
    private String email;
    
    @Override
    public String getEmail() {
        return email;
    }
}
