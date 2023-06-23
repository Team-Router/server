package team.router.recycle.domain.oauth;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.member.Member.Type;
import team.router.recycle.web.oauth.OauthLoginRequest;

@Service
public class RequestOauthService {
    private final Map<Type, OauthClient> clients;

    public RequestOauthService(List<OauthClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toUnmodifiableMap(OauthClient::getMemberType, Function.identity()));
    }

    public OauthInfo request(OauthLoginRequest oauthLoginRequest) {
        OauthClient oauthClient = clients.get(oauthLoginRequest.memberType());
        String accessToken = oauthClient.getOauthAccessToken(oauthLoginRequest);
        OauthProfileResponse oauthProfile = oauthClient.getOauthProfile(accessToken);

        return OauthInfo.builder()
                .email(oauthProfile.getEmail())
                .type(oauthLoginRequest.memberType())
                .build();
    }
}