package team.router.recycle.domain.oauth;

import lombok.Builder;
import team.router.recycle.domain.member.Member;

@Builder
public record OauthInfo(String email, Member.Type type) {

}
