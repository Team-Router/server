package team.router.recycle.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.oauth.OauthClient;
import team.router.recycle.domain.oauth.OauthInfo;
import team.router.recycle.domain.oauth.OauthProfileResponse;
import team.router.recycle.domain.oauth.google.GoogleOauthService;
import team.router.recycle.domain.oauth.kakao.KakaoOauthService;
import team.router.recycle.web.oauth.GoogleLoginRequest;
import team.router.recycle.web.oauth.KakaoLoginRequest;
import team.router.recycle.web.oauth.OauthLoginRequest;

import static org.assertj.core.api.Assertions.assertThat;

class OauthServiceTest {

    @Nested
    @DisplayName("구글 로그인 테스트")
    class GoogleOauthServiceTest {

        @DisplayName("로그인 성공시 사용자 정보를 가져올 수 있다.")
        @Test
        void getGoogleInfo() {
            GoogleOauthService googleOauthService = new GoogleOauthService(new GoogleOauthServiceTest.GoogleMockClient());

            OauthInfo googleInfo = googleOauthService.getGoogleInfo(new GoogleLoginRequest());

            assertThat(googleInfo.email()).isEqualTo("test@test.com");
            assertThat(googleInfo.type()).isEqualTo(Member.Type.GOOGLE);
        }

        static class GoogleMockClient implements OauthClient {

            @Override
            public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
                return "test-token";
            }

            @Override
            public OauthProfileResponse getOauthProfile(String accessToken) {
                return new GoogleOauthServiceTest.GoogleMockInfo();
            }

            @Override
            public Member.Type getMemberType() {
                return null;
            }
        }

        static class GoogleMockInfo implements OauthProfileResponse {

            @Override
            public String getEmail() {
                return "test@test.com";
            }
        }
    }

    @Nested
    @DisplayName("카카오 로그인")
    class KakaoOauthServiceTest {
        @DisplayName("로그인 성공시 사용자 정보를 가져올 수 있다.")
        @Test
        void getKakaoInfo() {
            KakaoOauthService kakaoOauthService = new KakaoOauthService(new KakaoOauthServiceTest.KakaoMockClient());

            OauthInfo kakaoInfo = kakaoOauthService.getKakaoInfo(new KakaoLoginRequest());

            assertThat(kakaoInfo.email()).isEqualTo("test@test.com");
            assertThat(kakaoInfo.type()).isEqualTo(Member.Type.KAKAO);
        }

        static class KakaoMockClient implements OauthClient {

            @Override
            public String getOauthAccessToken(OauthLoginRequest oauthLoginRequest) {
                return "test-token";
            }

            @Override
            public OauthProfileResponse getOauthProfile(String accessToken) {
                return new KakaoOauthServiceTest.KakaoMockInfo();
            }

            @Override
            public Member.Type getMemberType() {
                return null;
            }
        }

        static class KakaoMockInfo implements OauthProfileResponse {

            @Override
            public String getEmail() {
                return "test@test.com";
            }
        }
    }
}
