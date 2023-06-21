package team.router.recycle.domain.Member;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class MemberRequest {

    String email;
    String password;


    @Data
    @NoArgsConstructor
    public static class signUp {
        private String email;
        private String password;
    }
    @Data
    @NoArgsConstructor
    public static class signIn{
        private String email;
        private String password;
    }

}
