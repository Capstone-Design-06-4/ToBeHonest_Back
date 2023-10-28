package Team4.TobeHonest.controller;

import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.dto.signup.LoginDTO;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.login.LoginService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class HomeController {

    private final MemberService memberService;
    private final LoginService loginService;

    @GetMapping("/signup")
    @ResponseBody
    public String singUpForm(@AuthenticationPrincipal UserDetails user) {
        return "회원가입 폼 주세요";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> join(@Valid @RequestBody JoinDTO joinDTO, BindingResult bindingResult) {
        //field 관련 에러 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = memberService.displayLoginError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        //중복 회원 발생
        try {
            memberService.join(joinDTO);
        } catch (DuplicateMemberException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        //세션
        return ResponseEntity.ok("signUp Finished!");
    }


    @GetMapping("/findEmail")
    public String findEmailForm() {
        return "ID찾는 폼주세요";
    }

    @PostMapping("/login")

    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        try {
            TokenInfo login = loginService.login(email, password);
            return ResponseEntity.status(HttpStatus.OK).body(login);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
