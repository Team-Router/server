package team.router.recycle.domain.oauth.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import team.router.recycle.domain.oauth.OauthProfileResponse;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMyInfo implements OauthProfileResponse {

    @JsonProperty("emailAddresses")
    private List<EmailAddress> emailAddresses;

    @Override
    public String getEmail() {
        return emailAddresses.get(0).value;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EmailAddress {
        private String value;
    }
}
