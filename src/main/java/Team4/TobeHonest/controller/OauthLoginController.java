package Team4.TobeHonest.controller;

import Team4.TobeHonest.service.login.OauthLogin.KakaoLoginService;
import Team4.TobeHonest.service.login.OauthLogin.NaverLoginService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
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
@RequestMapping("/oauth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class OauthLoginController {

    private final NaverLoginService naverService;
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/naver")
    public String naverConnect() throws UnsupportedEncodingException {
        String url = naverService.createURL();
        return url; // 프론트 브라우저로 보내는 주소
    }

    @GetMapping("/kakao")
    public String kakaoConnect() throws UnsupportedEncodingException {
        String url = kakaoLoginService.createURL();
        return url; // 프론트 브라우저로 보내는 주소
    }


    @GetMapping("/naver-login")
    public ResponseEntity<?> naverLogin(@RequestParam String accessToken) throws JsonProcessingException {
        String email = naverService.login(accessToken);
        TokenInfo login = naverService.tokenInfo(email);
        return ResponseEntity.status(HttpStatus.OK).body(login);

    }

    @GetMapping("/kakao-login")
    public ResponseEntity<?> kakaoLogin(@RequestParam String accessToken) throws JsonProcessingException {
        String email = kakaoLoginService.login(accessToken);

        TokenInfo tokenInfo = kakaoLoginService.tokenInfo(email);
        return ResponseEntity.status(HttpStatus.OK).body(tokenInfo);
    }
}
