//package team.router.recycle.domain.oauth.google;
//
//import feign.Body;
//import feign.Headers;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.PostMapping;
//import team.router.recycle.domain.oauth.kakao.KakaoToken;
//import team.router.recycle.web.oauth.OauthLoginRequest;
//
//@FeignClient(name = "googleAuthClient", url = "${oauth.google.url.auth}")
//@Component
//public interface GoogleAuthClient extends AuthClient {
//    @PostMapping("/oauth/token")
//    @Headers("Content-Type: application/x-www-form-urlencoded, charset=utf-8")
//    @Body("grant_type=authorization_code&client_id={clientId}&redirect_uri={redirectUri}&code={authorizeCode}")
//    ResponseEntity<KakaoToken> getOauthToken(OauthLoginRequest oauthLoginRequest);
//
////    @PostMapping("/token")
////    ResponseEntity<GoogleToken> getOauthAccessToken(OauthLoginRequest oauthLoginRequest);
//}
//
