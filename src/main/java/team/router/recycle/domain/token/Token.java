//package team.router.recycle.domain.token;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.security.authentication.BadCredentialsException;
//
//import java.time.LocalDateTime;
//
//@Data
//@NoArgsConstructor
//@Entity
//public class Token {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    public Integer id;
//
//    @Column(name = "token_key")
//    private String key;
//
//    @Column(name = "token_value")
//    private String value;
//
//    @Column(name = "expired_at")
//    private LocalDateTime expiredAt;
//
//    public Token(String key, String value, LocalDateTime expiredAt) {
//        this.key = key;
//        this.value = value;
//        this.expiredAt = expiredAt;
//    }
//
//    public boolean isNotEqualTo(String value) {
//        return !this.value.equals(value);
//    }
//
//    public void renewValue(String value, LocalDateTime expiredAt) {
//        this.value = value;
//        this.expiredAt = expiredAt;
//    }
//
//    public void validateExpiredAt() {
//        if (this.expiredAt.isBefore(LocalDateTime.now())) {
//            throw new BadCredentialsException("토큰 만료 기간이 지났습니다. txId: " + key);
//        }
//    }
//
//    public void validateValue(String value) {
//        if (isNotEqualTo(value)) {
//            throw new BadCredentialsException("토큰 값이 다릅니다. value: " + value);
//        }
//    }
//}
