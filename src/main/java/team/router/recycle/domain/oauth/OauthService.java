package team.router.recycle.domain.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.router.recycle.web.auth.TokenResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

@Service
@RequiredArgsConstructor
public class OauthService {
    
//    private final OauthMemberService oauthMemberService;
    private final RequestOauthService requestOauthService;

    public TokenResponse login(OauthLoginRequest oauthLoginRequest) {
        OauthInfo oauthInfo = requestOauthService.request(oauthLoginRequest);
//        return oauthMemberService.getAccessTokenWithOauthInfo(oauthInfo);
        return null;
    }
}
