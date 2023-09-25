package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.JoinDTO;
import Team4.TobeHonest.dto.LoginDTO;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.setting.login.SessionConst;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;


    @GetMapping("/login")
    public String loginForm() {
        return "로그인 폼 주세요";
    }


    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        //field 관련 에러 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = memberService.displayLoginError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        Member loginMember = memberService.login(loginDTO.getEmail(), loginDTO.getPassWord());
        //로그인이 안되는 경우..
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("wrong email or password");
        }

        //세션
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        Cookie cookie = new Cookie(SessionConst.SESSION_COOKIE_NAME, session.getId());
        response.addCookie(cookie);
        return ResponseEntity.ok(loginMember.getName() + "님 반가워요!");
    }


    @PostMapping("/logout")
    public String login(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login";
    }

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

    @GetMapping("/findEmail")
    public String findEmailForm(){
        return "ID찾는 폼주세요";
    }




}
