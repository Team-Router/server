//package team.router.recycle.domain.token;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//import team.router.recycle.domain.jwt.TokenProvider;
//import team.router.recycle.web.auth.TokenRequest;
//import team.router.recycle.web.auth.TokenResponse;
//
//import java.time.LocalDateTime;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class RefreshTokenService implements TokenService {
//
//    private final TokenRepository tokenRepository;
//    private final TokenProvider tokenProvider;
//
//    @Override
//    public Token issueToken(String key, String value) {
//        Token token = RefreshToken.builder()
//                .key(key)
//                .value(value)
//                .expiredAt(LocalDateTime.now().plusWeeks(2))
//                .build();
//
//        tokenRepository.save(token);
//        return token;
//    }
//
//    @Override
//    public Token findToken(String key) {
//        return tokenRepository.findFirstByKeyOrderByIdDesc(key)
//                .orElseThrow(() -> new BadCredentialsException("로그아웃 된 사용자입니다."));
//    }
//
//    @Transactional
//    public TokenResponse reissue(TokenRequest tokenRequestDto) {
//        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
//            throw new BadCredentialsException("Refresh Token 이 유효하지 않습니다.");
//        }
//        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
//
//        Token refreshToken = findToken(authentication.getName());
//
//        if (refreshToken.isNotEqualTo(tokenRequestDto.getRefreshToken())) {
//            throw new BadCredentialsException("토큰의 유저 정보가 일치하지 않습니다.");
//        }
//
//        TokenResponse tokenDto = tokenProvider.generateTokenDto(authentication.getName(), authentication.getAuthorities());
//
//        refreshToken.renewValue(tokenDto.getRefreshToken(), LocalDateTime.now().plusWeeks(2));
//
//        return tokenDto;
//    }
//}
