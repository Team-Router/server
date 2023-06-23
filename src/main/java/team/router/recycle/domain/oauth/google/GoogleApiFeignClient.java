package team.router.recycle.domain.oauth.google;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleApiFeignClient", url = "${oauth.google.url.api}")
public interface GoogleApiFeignClient {

    @GetMapping("/v1/people/me?personFields=nicknames,emailAddresses,names")
    ResponseEntity<GoogleMyInfo> getOauthProfile(@RequestHeader("Authorization") String authorization);
}
