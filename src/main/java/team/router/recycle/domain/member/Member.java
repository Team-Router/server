package team.router.recycle.domain.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import team.router.recycle.domain.BaseEntity;
import team.router.recycle.util.BooleanYNConverter;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @Enumerated(EnumType.STRING)
    private Type type;
    @Enumerated(EnumType.STRING)
    private Authority authority = Authority.ROLE_USER;

    @Convert(converter = BooleanYNConverter.class)
    private Boolean isDeleted = Boolean.FALSE;

    @Builder
    public Member(String email, Type type, Boolean isDeleted) {
        this.email = email;
        this.type = type;
        this.isDeleted = isDeleted;
    }

    public enum Type {
        GOOGLE, NAVER, KAKAO
    }

    public enum Authority {
        ROLE_USER, ROLE_ADMIN
    }

    public void delete() {
        this.isDeleted = Boolean.TRUE;
        deletedAt = LocalDateTime.now();
    }
}
