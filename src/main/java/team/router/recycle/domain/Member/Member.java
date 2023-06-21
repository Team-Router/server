package team.router.recycle.domain.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String password;

    Boolean isDeleted = Boolean.FALSE;

    public Member(String email, String password, Boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.isDeleted = isDeleted;
    }
}
