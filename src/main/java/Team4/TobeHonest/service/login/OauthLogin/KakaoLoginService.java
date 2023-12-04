package Team4.TobeHonest.service.login.OauthLogin;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.login.KakaoProfileVo;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.utils.jwt.JwtTokenProvider;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginService {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private String clientId = "c91d8a66aca04badaf1b630ae744dea3";


    public String createURL() throws UnsupportedEncodingException {
        String redirectURI = URLEncoder.encode("http://52.78.37.19:8080/oauth/kakao-login", "UTF-8"); // redirectURI 인코딩

        String url = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectURI +
                "&response_type=code";

        return url;
    }

    @Transactional
    public String login(String accessToken) throws JsonProcessingException {
        // 네이버 로그인 Token 발급 API 요청을 위한 header/parameters 설정 부분


        // 토큰을 이용해 정보를 받아올 API 요청을 보낼 로직 작성하기
        RestTemplate profile_rt = new RestTemplate();
        HttpHeaders userDetailReqHeaders = new HttpHeaders();
        userDetailReqHeaders.add("Authorization", "Bearer " + accessToken);
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

        ObjectMapper profile_om = new ObjectMapper();
        KakaoProfileVo kakaoProfileVo = null;
        try {
            kakaoProfileVo = profile_om.readValue(userDetailResponse.getBody(), KakaoProfileVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }

        Member member = memberService.findByEmailWithNoException(kakaoProfileVo.getKakao_account().getEmail());
        if (member == null) {
            member = dtoToEntity(kakaoProfileVo);
            memberService.joinMember(member);
        }
        return member.getEmail();

    }


    private Member dtoToEntity(KakaoProfileVo vo) {


//        String randPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        return Member.builder()
                .email(vo.getKakao_account().getEmail())
                .name(vo.getKakao_account().getProfile().getNickname())
                .password(passwordEncoder.encode("randPassword"))
                .build();

    }


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
