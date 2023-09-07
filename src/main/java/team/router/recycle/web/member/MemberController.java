package team.router.recycle.web.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.router.recycle.domain.member.MemberService;
import team.router.recycle.util.SecurityUtil;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @DeleteMapping
    public ResponseEntity<?> memberDelete() {
        memberService.delete(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.ok().build();
    }
}
