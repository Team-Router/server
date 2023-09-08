package team.router.recycle.domain.oauth;

import team.router.recycle.domain.member.Member;

public record OauthInfo(String email, Member.Type type) {
    public static OauthInfo of(String email, Member.Type type) {
        return new OauthInfo(email, type);
    }
}
