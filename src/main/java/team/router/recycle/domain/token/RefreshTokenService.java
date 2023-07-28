package team.router.recycle.domain.token;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import team.router.recycle.domain.jwt.TokenProvider;
import team.router.recycle.web.auth.TokenRequest;
import team.router.recycle.web.auth.TokenResponse;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService implements TokenService {

    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    public Token issueToken(String key, String value) {
        tokenRepository.findByKey(key)
                .ifPresent(tokenRepository::delete);
        Token token = RefreshToken.builder()
                .key(key)
                .value(value)
                .expiredAt(LocalDateTime.now().plusWeeks(2))
                .build();

        tokenRepository.save(token);
        return token;
    }

    @Transactional
    public TokenResponse reissue(TokenRequest tokenRequestDto) {
        if (!tokenProvider.validateToken(tokenRequestDto.refreshToken())) {
            throw new BadCredentialsException("Refresh Token 이 유효하지 않습니다.");
        }
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.accessToken());

        Token refreshToken = tokenRepository.findByKey(authentication.getName()).orElseThrow(
                () -> new RecycleException(ErrorCode.UNAUTHORIZED,"로그아웃 된 사용자입니다.")
        );

        if (refreshToken.isNotEqualTo(tokenRequestDto.refreshToken())) {
            throw new BadCredentialsException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        TokenResponse tokenDto = tokenProvider.generateTokenDto(authentication.getName(), authentication.getAuthorities());

        refreshToken.renewValue(tokenDto.refreshToken(), LocalDateTime.now().plusWeeks(2));

        return tokenDto;
    }
}
