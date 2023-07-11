package team.router.recycle.web.auth;

import lombok.Builder;

@Builder
public record TokenResponse(String accessToken, String refreshToken, Long accessTokenExpiresIn) {
}
