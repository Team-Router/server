package team.router.recycle.domain.oauth;

import team.router.recycle.domain.member.Member;
import team.router.recycle.web.oauth.OauthLoginRequest;

public interface AuthClient {

    String getOauthAccessToken(OauthLoginRequest oauthLoginRequest);

    Member.Type getMemberType();
}
