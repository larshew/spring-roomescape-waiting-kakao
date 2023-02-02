package roomescape.nextstep.member;

import roomescape.auth.AuthorizationException;
import roomescape.auth.LoginMember;
import roomescape.auth.userauth.UserAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/members")
public class MemberController {
    @GetMapping("/haha")
    public ResponseEntity<Void> haha(){
        System.out.println("HahaHaha");
        throw new AuthorizationException();
    }
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> createMember(@RequestBody MemberRequest memberRequest) {
        Long id = memberService.create(memberRequest);
        return ResponseEntity.created(URI.create("/members/" + id)).build();
    }


    @GetMapping("/me")
    public ResponseEntity<UserAuth> me(@LoginMember UserAuth userAuth) {
        return ResponseEntity.ok(userAuth);
    }
}
