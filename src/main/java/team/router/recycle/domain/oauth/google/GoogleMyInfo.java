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

    @JsonProperty("nicknames")
    private List<Nickname> nicknames;

    @JsonProperty("names")
    private List<Name> names;

    @Override
    public String getEmail() {
        return emailAddresses.get(0).value;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class EmailAddress {
        private String value;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Nickname {
        private String value;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Name {
        private String displayName;
    }

//    @Override
//    public String getNickName() {
//        if (nicknames == null || nicknames.isEmpty()) {
//            return names.get(0).displayName;
//        }
//
//        return nicknames.get(0).value;
//    }
}
