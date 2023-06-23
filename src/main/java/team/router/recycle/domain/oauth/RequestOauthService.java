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
    private final Map<Type, OauthApiClient> clients;

    public RequestOauthService(List<OauthApiClient> clients) {
        this.clients = clients.stream()
                .collect(Collectors.toUnmodifiableMap(OauthApiClient::getMemberType, Function.identity()));
    }

    public OauthInfo request(OauthLoginRequest oauthLoginRequest) {
        OauthApiClient oauthApiClient = clients.get(oauthLoginRequest.memberType());
        String accessToken = oauthApiClient.getOauthAccessToken(oauthLoginRequest);
        OauthProfileResponse oauthProfile = oauthApiClient.getOauthProfile(accessToken);

        return OauthInfo.builder()
                .email(oauthProfile.getEmail())
                .type(oauthLoginRequest.memberType())
                .build();
    }
}