package Team4.TobeHonest.controller;


import Team4.TobeHonest.dto.JoinDTO;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
//멤버 로그인, 회원가입
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity join(@Valid @RequestBody JoinDTO joinDTO, BindingResult bindingResult) {
        //field 관련 에러 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = memberService.displayLoginError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        //중복 회원 발생
        try {
            memberService.join(joinDTO.toMember());
        } catch (DuplicateMemberException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate email");
        }
        //세션
        return ResponseEntity.ok("join Finished!");

    }


//    @GetMapping("/login")

    /*public String loginForm(){
        return
    }*/


}
