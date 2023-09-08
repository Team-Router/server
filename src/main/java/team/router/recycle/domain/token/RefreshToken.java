package team.router.recycle.domain.token;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class RefreshToken extends Token {

    private RefreshToken(String key, String value, LocalDateTime expiredAt) {
        super(key, value, expiredAt);
    }

    public static RefreshToken of(String key, String value, LocalDateTime expiredAt) {
        return new RefreshToken(key, value, expiredAt);
    }
}
