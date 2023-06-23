package team.router.recycle.web.oauth;

import org.springframework.util.MultiValueMap;
import team.router.recycle.domain.member.Member;

public interface OauthLoginRequest {
    MultiValueMap<String, String> makeBody();

    Member.Type memberType();
}