//package team.router.recycle.web.auth;
//
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import team.router.recycle.domain.token.Token;
//
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class UpdateTokenDto {
//    private String txId;
//    private String email;
//
//    public static UpdateTokenDto of(Token token) {
//        return UpdateTokenDto.builder()
//                .txId(token.getKey())
//                .email(token.getValue())
//                .build();
//    }
//}
