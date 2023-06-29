package team.router.recycle.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.router.recycle.domain.jwt.TokenProvider;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberRepository;
import team.router.recycle.domain.oauth.OauthInfo;
import team.router.recycle.domain.oauth.OauthMemberService;
import team.router.recycle.web.auth.TokenResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
class OauthMemberServiceTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private OauthMemberService oauthMemberService;

    @Nested
    @DisplayName("소셜 로그인 테스트 - 회원 가입이 되어 있지 않으면 가입 처리")
    class Signup {

        @DisplayName("카카오로 로그인 테스트 - 회원 가입이 되어 있지 않으면 가입 처리")
        @Test
        void testKakaoSignup() {
            // given
            String email = "test@kakao.com";
            OauthInfo oauthInfo = OauthInfo.builder()
                    .email(email)
                    .type(Member.Type.KAKAO)
                    .build();

            TokenResponse tokenResponse = oauthMemberService.getAccessTokenWithOauthInfo(oauthInfo);

            assertThat(tokenResponse.getAccessToken()).isNotNull();
            assertThat(tokenResponse.getRefreshToken()).isNotNull();
            assertThat(tokenResponse.getGrantType()).isNotNull();
            assertThat(tokenResponse.getAccessTokenExpiresIn()).isNotNull();

            String accessToken = tokenResponse.getAccessToken();

            assertThat(tokenProvider.validateToken(accessToken)).isTrue();

            long memberId = Long.parseLong(tokenProvider.getAuthentication(accessToken).getName());
            Member member = memberRepository.findByEmail(email).get();
            assertThat(member.getId()).isEqualTo(memberId);
        }
    }
}