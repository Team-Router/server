//package team.router.recycle.domain.auth;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import team.router.recycle.domain.jwt.TokenProvider;
//import team.router.recycle.domain.member.Member;
//import team.router.recycle.domain.member.MemberService;
//import team.router.recycle.domain.token.RefreshTokenService;
//import team.router.recycle.web.auth.*;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final TokenProvider tokenProvider;
//    private final RefreshTokenService refreshTokenService;
//    private final MemberService memberService;
//    private final PasswordEncoder passwordEncoder;
//
//
//    public SignUpResponse signUp(SignUpRequest signUpDto) {
//        if (memberService.existsEmail(signUpDto.getEmail())) {
//            throw new DuplicateKeyException("이미 존재하는 유저입니다. email: " + signUpDto.getEmail());
//        }
//
//        Member member = signUpDto.toMember(passwordEncoder);
//        memberService.save(member);
//        return SignUpResponse.of(member);
//    }
//
//    public TokenResponse signIn(SignInRequest signInDto) {
//        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성
//        UsernamePasswordAuthenticationToken authenticationToken = signInDto.toAuthentication();
//
//        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
//        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//        // 3. 인증 정보를 기반으로 JWT 토큰 생성
//        TokenResponse tokenDto = tokenProvider.generateTokenDto(authentication.getName(), authentication.getAuthorities());
//
//        // 4. RefreshToken 저장
//        refreshTokenService.issueToken(authentication.getName(), tokenDto.getRefreshToken());
//
//        // 5. 토큰 발급
//        return tokenDto;
//    }
//
//    public TokenResponse reissue(TokenRequest tokenRequestDto) {
//        return refreshTokenService.reissue(tokenRequestDto);
//    }
//}
