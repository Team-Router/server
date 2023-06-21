package team.router.recycle.domain.Member;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import team.router.recycle.Response;

import java.util.Optional;

@Service
public class MemberService {
    private final Response response;

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MemberService(Response response, MemberRepository memberRepository) {
        this.response = response;
        this.memberRepository = memberRepository;
    }

    /**\
     * 회원 가입
     * @param signUp
     * @return
     */
    public ResponseEntity<?> signUp(MemberRequest.signUp signUp) {
        // 이메일 중복 체크
        Optional<Member> existingMember = memberRepository.findByEmail(signUp.getEmail());
        if (existingMember.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하는 회원 입니다.");
        }

        // 비밀번호 해시 처리
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(signUp.getPassword());

        Member member = new Member(signUp.getEmail(), hashedPassword, false);
        memberRepository.save(member);

        return ResponseEntity.ok(member);
    }



    /**
     * 회원 탈퇴
     * @param memberId
     * @return
     */
    public ResponseEntity<?> signOut(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotFoundException("맴버id를 확인 할 수 없습니다. " + memberId)
        );
        member.setIsDeleted(true);
        return response.success();

    }

    /**
     * 회원 로그인
     * @param signIn
     * @return
     */
    public ResponseEntity<?> signIn(MemberRequest.signIn signIn) {
        Member member = memberRepository.findByEmail(signIn.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("맴버id를 확인 할 수 없습니다. " + signIn)
        );

        if (!passwordEncoder.matches(signIn.getPassword(), member.password)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 맞지 않습니다.");
        }

        if (member.getIsDeleted()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("탈퇴한 유저 입니다.");
        }

        return ResponseEntity.ok("Login을 성공하였습니다.");
    }


    /**
     * 회원 로그아웃
     */
    // 임시
    public ResponseEntity<?> logout(MemberRequest.signIn signIn) {
        return ResponseEntity.ok().body("");
    }

}
