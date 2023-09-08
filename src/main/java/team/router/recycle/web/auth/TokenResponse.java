package team.router.recycle.web.auth;

import java.util.Date;

public record TokenResponse(String accessToken, String refreshToken, Long accessTokenExpiresIn) {
    public static TokenResponse of(String accessToken, String refreshToken, Date accessTokenExpiresIn) {
        return new TokenResponse(accessToken, refreshToken, accessTokenExpiresIn.getTime());
    }
}
