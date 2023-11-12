package Team4.TobeHonest.service.login.OauthLogin;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.login.KakaoProfileVo;
import Team4.TobeHonest.dto.login.KakaoTokenVo;
import Team4.TobeHonest.dto.login.NaverProfileVo;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.utils.jwt.JwtTokenProvider;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService implements OAuthLoginService{
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private String clientId = "c91d8a66aca04badaf1b630ae744dea3";

    @Override
    public String createURL() throws UnsupportedEncodingException {
        String redirectURI = URLEncoder.encode("http://localhost:8080/oauth/kakao-login", "UTF-8"); // redirectURI 인코딩

        String url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectURI +
                "&response_type=code";

        return url;
    }

    @Override
    @Transactional
    public String login(String code, String state, HttpServletResponse response) throws JsonProcessingException {
        // 네이버 로그인 Token 발급 API 요청을 위한 header/parameters 설정 부분
        RestTemplate token_rt = new RestTemplate(); // REST API 요청용 Template

        HttpHeaders naverTokenRequestHeadres = new HttpHeaders();  // Http 요청을 위한 헤더 생성
        naverTokenRequestHeadres.add("Content-type", "application/x-www-form-urlencoded"); // application/json 했다가 grant_type missing 오류남 (출력포맷만 json이라는 거엿음)

        // 파라미터들을 담아주기위한 맵 (파라미터용이기 때문에, 따로 앞에 ?나 &나 =같은 부호를 입력해주지 않아도 됨. 오히려 넣으면 인식못함)
        // 네이버 가이드에서 요청하는 파라미터들 (Developers 참고)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(params, naverTokenRequestHeadres);

        // 서비스 서버에서 네이버 인증 서버로 요청 전송(POST 또는 GET이라고 공식문서에 있음), 응답은 Json으로 제공됨
        ResponseEntity<String> oauthTokenResponse = token_rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // body로 access_token, refresh_token, token_type:bearer, expires_in:3600 온 상태

        // oauthTokenResponse로 받은 토큰정보 객체화
        ObjectMapper token_om = new ObjectMapper();
        KakaoTokenVo kakaoTokenVO = null;
        try {
            kakaoTokenVO = token_om.readValue(oauthTokenResponse.getBody(), KakaoTokenVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }


        // 토큰을 이용해 정보를 받아올 API 요청을 보낼 로직 작성하기
        RestTemplate profile_rt = new RestTemplate();
        HttpHeaders userDetailReqHeaders = new HttpHeaders();
        userDetailReqHeaders.add("Authorization", "Bearer " + kakaoTokenVO.getAccess_token());
        userDetailReqHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(userDetailReqHeaders);

        // 서비스서버 - kakao 인증서버 : 유저 정보 받아오는 API 요청
        ResponseEntity<String> userDetailResponse = profile_rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        // 요청 응답 확인
        log.info(userDetailResponse.toString());

        ObjectMapper profile_om = new ObjectMapper();
        KakaoProfileVo kakaoProfileVo = null;
        try {
            kakaoProfileVo = profile_om.readValue(userDetailResponse.getBody(), KakaoProfileVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }

        Member member = memberService.findByEmail(kakaoProfileVo.getKakao_account().getEmail());
        if (member == null){
            member = dtoToEntity(kakaoProfileVo);
            memberService.joinMember(member);
        }
        return member.getEmail();

    }



    private Member dtoToEntity(KakaoProfileVo vo){



//        String randPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        return Member.builder()
                .email(vo.getKakao_account().getEmail())
                .name(vo.getKakao_account().getProfile().getNickname())
                .password(passwordEncoder.encode("randPassword"))
                .build();

    }

    @Override
    @Transactional
    public TokenInfo tokenInfo(String memberEmail) {
        Member member = memberService.findByEmail(memberEmail);
        UsernamePasswordAuthenticationToken authenticationToken
                //randPassword는 당연히 수정해야하는 문제..
                = new UsernamePasswordAuthenticationToken(member.getEmail(), "randPassword");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        redisTemplate.opsForValue().set(
                "JWT_TOKEN:" + member.getEmail(), tokenInfo.getAccessToken());
        return tokenInfo;
    }

}
