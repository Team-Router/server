package team.router.recycle.web.oauth;

import org.springframework.util.MultiValueMap;
import team.router.recycle.domain.member.Member;

public interface OauthLoginRequest {
    
    Member.Type memberType();
    
    MultiValueMap<String, String> makeBody();
}
