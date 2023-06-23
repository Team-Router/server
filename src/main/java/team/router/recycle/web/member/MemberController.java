package team.router.recycle.web.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.member.MemberService;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 가입
//    @PostMapping("/signup")
//    public ResponseEntity<?> signUp(@RequestBody MemberRequest.signUp signUp){
//        return memberService.signUp(signUp);
//    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> signOut(@PathVariable Long memberId) {
        memberService.signOut(memberId);
        return ResponseEntity.ok().build();
    }

    // 회원 로그인
//    @PostMapping("/signin")
//    public ResponseEntity<?> signIn(@RequestBody MemberRequest.signIn signIn){
//        return memberService.signIn(signIn);
//    }


    // 회원 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestBody MemberRequest.signIn signIn){
//        return memberService.logout(signIn);
//    }
}
