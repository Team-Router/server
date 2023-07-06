package team.router.recycle.domain.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;
import team.router.recycle.web.auth.TokenResponse;
import team.router.recycle.web.oauth.OauthLoginRequest;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final OauthMemberService oauthMemberService;
    private final RequestOauthService requestOauthService;
    private final Response response;

    public ResponseEntity<?> login(OauthLoginRequest oauthLoginRequest) {
        OauthInfo oauthInfo = requestOauthService.request(oauthLoginRequest);
        return response.success(oauthMemberService.getAccessTokenWithOauthInfo(oauthInfo));
    }
}
