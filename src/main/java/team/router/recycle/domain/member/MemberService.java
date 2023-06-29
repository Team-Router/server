package team.router.recycle.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import team.router.recycle.web.member.MemberResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberResponse findMemberInfoById(Long memberId) {
        return memberRepository.findById(memberId)
                .map(MemberResponse::of)
                .orElseThrow(() -> new EmptyResultDataAccessException("회원 정보가 없습니다. memberId: " + memberId, 1));
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EmptyResultDataAccessException("회원 정보가 없습니다. email: " + email, 1));
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new EmptyResultDataAccessException("회원 정보가 없습니다. memberId: " + memberId, 1));
    }

    public Optional<Member> findOptionalByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public boolean existsEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public void signOut(Long memberId) {
        Member member = findById(memberId);
        member.setIsDeleted(Boolean.TRUE);
    }
}
