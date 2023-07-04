package team.router.recycle.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.router.recycle.util.BooleanYNConverter;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private final Authority authority = Authority.ROLE_USER;

    @Convert(converter = BooleanYNConverter.class)
    private Boolean isDeleted = Boolean.FALSE;

    @Builder
    public Member(String email, Type type, Boolean isDeleted) {
        this.email = email;
        this.type = type;
        this.isDeleted = isDeleted;
    }

    public void delete() {
        this.isDeleted = Boolean.TRUE;
    }

    public enum Type {
        GOOGLE, NAVER, KAKAO
    }

    public enum Authority {
        ROLE_USER, ROLE_ADMIN
    }
}
