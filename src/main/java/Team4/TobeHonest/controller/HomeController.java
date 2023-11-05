package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.dto.signup.LoginDTO;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.login.AuthService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class HomeController {

    private final MemberService memberService;
    private final AuthService loginService;

    @GetMapping("/signup")

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
        memberService.join(joinDTO);
        //세션
        return ResponseEntity.ok("signUp Finished!");
    }


    @GetMapping("/findEmail")
    public String findEmailForm() {
        return "ID찾는 폼주세요";
    }

    @GetMapping("/login")
    public String loginGet() {
        return "로그인하쇼";
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        TokenInfo login = loginService.login(email, password);
        Member member = memberService.findByEmail(email);
        request.getSession().setAttribute(email, member);
        return ResponseEntity.status(HttpStatus.OK).body(login);

    }

    @PostMapping("/Logout")
    public ResponseEntity<String> logout() {

        HttpHeaders headers = new HttpHeaders();
        String logout = loginService.logout();
        return ResponseEntity.status(HttpStatus.OK).body(logout);


    }


    /**
     * 프론트에 Redirect URI를 제공하기 위한 메소드
     * 프론트에서 네이버 인증 센터로 요청을 주기위한 URI를 제공하며, 이를통해 인증코드를 받아 자체 서비스 callback API 호출시 전달
     *
     * @return redirect URI
     * @throws UnsupportedEncodingException
     */


}
