package team.router.recycle.web.oauth;

import team.router.recycle.domain.member.Member;

public interface OauthLoginRequest {
    Member.Type memberType();
}