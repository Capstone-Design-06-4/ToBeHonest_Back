package Team4.TobeHonest.controller;

import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.login.NaverLoginService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/naver")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class NaverLoginController {

    private final NaverLoginService naverService;
    private final MemberService memberService;

    /**
     * 프론트에 Redirect URI를 제공하기 위한 메소드
     * 프론트에서 네이버 인증 센터로 요청을 주기위한 URI를 제공하며, 이를통해 인증코드를 받아 자체 서비스 callback API 호출시 전달
     *
     * @return redirect URI
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/oauth")
    public ResponseEntity<?> naverConnect() throws UnsupportedEncodingException {
        String url = naverService.createNaverURL();

        return new ResponseEntity<>(url, HttpStatus.OK); // 프론트 브라우저로 보내는 주소
    }

    @GetMapping("/callback")
    public ResponseEntity<?> naverLogin(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) throws JsonProcessingException, JsonProcessingException {
        String email = naverService.loginNaver(code, state, response);
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
}
