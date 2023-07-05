package team.router.recycle.domain.oauth;

import team.router.recycle.domain.member.Member;
import team.router.recycle.web.oauth.OauthLoginRequest;

public interface OAuthClient {

    String getOauthAccessToken(OauthLoginRequest oauthLoginRequest);

    OauthProfileResponse getOauthProfile(String accessToken);

    Member.Type getMemberType();
}
