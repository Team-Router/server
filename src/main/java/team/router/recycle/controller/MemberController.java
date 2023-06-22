package team.router.recycle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.router.recycle.domain.member.MemberRequest;
import team.router.recycle.domain.member.MemberService;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody MemberRequest.signUp signUp){

        return memberService.signUp(signUp);
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> signOut(@PathVariable Long memberId){
        return memberService.signOut(memberId);
    }

    // 회원 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody MemberRequest.signIn signIn){
        return memberService.signIn(signIn);
    }



    // 회원 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody MemberRequest.signIn signIn){
        return memberService.logout(signIn);
    }

}
