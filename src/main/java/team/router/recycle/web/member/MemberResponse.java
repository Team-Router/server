package team.router.recycle.web.member;

import lombok.Builder;
import lombok.Getter;
import team.router.recycle.domain.member.Member;

@Getter
@Builder
public class MemberResponse {
    private String email;
    private Member.Type type;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .type(member.getType())
                .build();
    }
}
