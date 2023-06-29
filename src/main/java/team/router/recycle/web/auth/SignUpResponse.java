package team.router.recycle.web.auth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.member.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String email;

    public static SignUpResponse of(Member member) {
        return new SignUpResponse(member.getEmail());
    }
}
