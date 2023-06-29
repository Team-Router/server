package team.router.recycle.domain.member;

import lombok.Data;
import lombok.NoArgsConstructor;


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
