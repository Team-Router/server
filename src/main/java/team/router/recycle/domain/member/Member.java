package team.router.recycle.domain.member;

import helper.BooleanYNConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Type type;

    @Convert(converter = BooleanYNConverter.class)
    private Boolean isDeleted = Boolean.FALSE;

    @Builder
    public Member(String email, String password, Type type, Boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.type = type;
        this.isDeleted = isDeleted;
    }


    public enum Type {
        GOOGLE, NAVER, KAKAO
    }
}
