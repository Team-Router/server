package team.router.recycle.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.router.recycle.web.exception.ErrorCode;
import team.router.recycle.web.exception.RecycleException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RecycleException(ErrorCode.MEMBER_NOT_FOUND, "해당 회원을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Optional<Member> findOptionalByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
