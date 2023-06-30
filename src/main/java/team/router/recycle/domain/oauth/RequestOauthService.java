package team.router.recycle.domain.oauth;

import org.springframework.stereotype.Service;
import team.router.recycle.domain.member.Member.Type;
import team.router.recycle.web.oauth.OauthLoginRequest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestOauthService {
    private final Map<Type, AuthClient> authClients;
    private final Map<Type, InfoClient> infoClients;

    public RequestOauthService(List<AuthClient> authClients, List<InfoClient> infoClients) {
        this.authClients = authClients.stream()
                .collect(Collectors.toMap(AuthClient::getMemberType, Function.identity()));
        this.infoClients = infoClients.stream()
                .collect(Collectors.toMap(InfoClient::getMemberType, Function.identity()));
    }

    public OauthInfo request(OauthLoginRequest oauthLoginRequest) {
        AuthClient authClient = authClients.get(oauthLoginRequest.memberType());
        InfoClient infoClient = infoClients.get(oauthLoginRequest.memberType());
        String accessToken = authClient.getOauthAccessToken(oauthLoginRequest);
        OauthProfileResponse oauthProfile = infoClient.getOauthProfile(accessToken);

        return OauthInfo.builder()
                .email(oauthProfile.getEmail())
                .type(oauthLoginRequest.memberType())
                .build();
    }
}