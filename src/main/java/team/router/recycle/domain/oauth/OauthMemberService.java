package team.router.recycle.domain.oauth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.domain.jwt.TokenProvider;
import team.router.recycle.domain.member.Member;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.domain.token.RefreshTokenService;
import team.router.recycle.web.auth.TokenResponse;

import java.util.Collection;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class OauthMemberService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public TokenResponse getAccessTokenWithOauthInfo(OauthInfo oauthInfo) {
        Member member = memberService.findOptionalByEmail(oauthInfo.email())
                .orElseGet(() -> newMember(oauthInfo));

        return generateToken(String.valueOf(member.getId()), getAuthorities(member));
    }

    private Member newMember(OauthInfo oauthInfo) {
        Member member = Member.builder()
                .type(oauthInfo.type())
                .email(oauthInfo.email())
                .build();

        return memberService.save(member);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());
        return Collections.singleton(grantedAuthority);
    }

    private TokenResponse generateToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        TokenResponse tokenResponse = tokenProvider.generateTokenDto(subject, authorities);
        refreshTokenService.issueToken(subject, tokenResponse.refreshToken());
        return tokenResponse;
    }
}
