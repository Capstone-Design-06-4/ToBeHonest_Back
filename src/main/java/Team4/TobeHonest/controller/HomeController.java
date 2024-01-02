package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.dto.signup.LoginDTO;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.login.AuthService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class HomeController {

    private final MemberService memberService;
    private final AuthService loginService;

    @PostMapping("/signup")
    //@RequestBody @Validated JoinDTO joinDTO ==> 순서 조심해라   
    public ResponseEntity<String> join(@RequestBody @Validated JoinDTO joinDTO, BindingResult bindingResult) {

        //field 관련 에러 확인
        //문제는 type에러가 일어나는 경우 controller자체가 실행되지 않아서 400에러를 띄우는 경우가 발생할 것이다.
        //ExpcetionHandler에 구현
        if (bindingResult.hasErrors()) {
            String errorMessage = displayError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        memberService.join(joinDTO);
        //세션
        return ResponseEntity.ok("signUp Finished!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            String errorMessage = displayError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();
        TokenInfo login = loginService.login(email, password);
        Member member = memberService.findByEmail(email);
        request.getSession().setAttribute(email, member);
        return ResponseEntity.status(HttpStatus.OK).body(login);

    }

    @PostMapping("/Logout")
    public ResponseEntity<String> logout() {

        String logout = loginService.logout();
        return ResponseEntity.status(HttpStatus.OK).body(logout);


    }


    private String displayError(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            FieldError fieldError = (FieldError) error;
            String message = error.getDefaultMessage();
            sb.append("field: ").append(fieldError.getField());
            sb.append("message: ").append(message);
        }
        return sb.toString();
    }
}
