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
    
    private final Map<Type, OAuthClient> client;
    
    public RequestOauthService(List<OAuthClient> clients) {
        this.client = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthClient::getMemberType, Function.identity())
        );
    }
    
    public OauthInfo request(OauthLoginRequest oauthLoginRequest) {
        OAuthClient oAuthClient = client.get(oauthLoginRequest.memberType()); // kakao
        String accessToken = oAuthClient.getOauthAccessToken(oauthLoginRequest);
        OauthProfileResponse oauthProfile = oAuthClient.getOauthProfile(accessToken);
        
        return OauthInfo.builder()
                .email(oauthProfile.getEmail())
                .type(oauthLoginRequest.memberType())
                .build();
    }
}
