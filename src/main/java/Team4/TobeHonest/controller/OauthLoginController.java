package Team4.TobeHonest.controller;

import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.login.OauthLogin.GoogleLoginService;
import Team4.TobeHonest.service.login.OauthLogin.KakaoLoginService;
import Team4.TobeHonest.service.login.OauthLogin.NaverLoginService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class OauthLoginController {

    private final NaverLoginService naverService;
    private final GoogleLoginService googleLoginService;
    private final KakaoLoginService kakaoLoginService;
    private final MemberService memberService;


    /**
     * 프론트에 Redirect URI를 제공하기 위한 메소드
     * 프론트에서 네이버 인증 센터로 요청을 주기위한 URI를 제공하며, 이를통해 인증코드를 받아 자체 서비스 callback API 호출시 전달
     *
     * @return redirect URI
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/naver")
    public RedirectView naverConnect() throws UnsupportedEncodingException {
        String url = naverService.createURL();
        return new RedirectView(url); // 프론트 브라우저로 보내는 주소
    }

    @GetMapping("/kakao")
    public RedirectView kakaoConnect() throws UnsupportedEncodingException {
        String url = kakaoLoginService.createURL();
        return new RedirectView(url); // 프론트 브라우저로 보내는 주소
    }





    @GetMapping("/naver-login")
    public ResponseEntity<?> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException, JsonProcessingException {
        String email = naverService.login(code, state, response);
        log.info("controller");
        log.info(email);
        try {
            TokenInfo login = naverService.tokenInfo(email);
            return ResponseEntity.status(HttpStatus.OK).body(login);

        } catch (Exception e) {
            log.info(e.getMessage());
            log.info(e.getClass().getName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
    @GetMapping("/kakao-login")
    public ResponseEntity<?> kakoLogin(@RequestParam("code") String code, @RequestParam(value = "state", required = false) String state, HttpServletResponse response) throws JsonProcessingException, JsonProcessingException {
        String email = kakaoLoginService.login(code, state, response);

        TokenInfo tokenInfo = kakaoLoginService.tokenInfo(email);
        log.info(String.valueOf(tokenInfo));
        return ResponseEntity.status(HttpStatus.OK).body(tokenInfo);


    }
}