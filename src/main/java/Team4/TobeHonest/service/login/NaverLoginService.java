package Team4.TobeHonest.service.login;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.login.NaverProfileVo;
import Team4.TobeHonest.dto.login.NaverTokenVo;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.utils.jwt.JwtTokenProvider;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NaverLoginService {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    String getNaverClientId;
    String getNaverClientSecret;
    @Autowired
    public NaverLoginService(MemberService memberService, @Value("${spring.oauth.naver.clientId}") String getNaverClientId,
                             @Value("${spring.oauth.naver.clientSecret}") String getNaverClientSecret,
                             MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, String> redisTemplate, AuthenticationManagerBuilder authenticationManagerBuilder, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.getNaverClientId = getNaverClientId;
        this.getNaverClientSecret = getNaverClientSecret;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.passwordEncoder = passwordEncoder;
    }

    public String createNaverURL() throws UnsupportedEncodingException {
        StringBuffer url = new StringBuffer();

        // 카카오 API 명세에 맞춰서 작성
        String redirectURI = URLEncoder.encode("http://www.localhost:8080/naver/callback", "UTF-8"); // redirectURI 설정 부분
        SecureRandom random = new SecureRandom();
        String state = new BigInteger(130, random).toString();

        url.append("https://nid.naver.com/oauth2.0/authorize?response_type=code");
        url.append("&client_id=" + getNaverClientId);
        url.append("&state=" + state);
        url.append("&redirect_uri=" + redirectURI);

    /* 로그인 중 선택 권한 허용 URL로 redirect 문제 해결하기
       로그인 시도시, "현재 UYouBooDan은 개발 중 상태입니다. 개발 중 상태에서는 등록된 아이디만 로그인할 수 있습니다." 화면으로 가버림.
       아래와 같은 URL로 리다이렉트 되도록 유도하는 해결책 찾기
       : https://nid.naver.com/oauth2.0/authorize?client_id=avgLtiDUfWMFfHpplTZh&redirect_uri=https://developers.naver.com/proxyapi/forum/auth/oAuth2&response_type=code&state=RZ760w
     */

        return url.toString();
    }
    @Transactional
    public String loginNaver(String code, String state, HttpServletResponse response) throws JsonProcessingException {
        // 네이버 로그인 Token 발급 API 요청을 위한 header/parameters 설정 부분
        RestTemplate token_rt = new RestTemplate(); // REST API 요청용 Template

        HttpHeaders naverTokenRequestHeadres = new HttpHeaders();  // Http 요청을 위한 헤더 생성
        naverTokenRequestHeadres.add("Content-type", "application/x-www-form-urlencoded"); // application/json 했다가 grant_type missing 오류남 (출력포맷만 json이라는 거엿음)

        // 파라미터들을 담아주기위한 맵 (파라미터용이기 때문에, 따로 앞에 ?나 &나 =같은 부호를 입력해주지 않아도 됨. 오히려 넣으면 인식못함)
        // 네이버 가이드에서 요청하는 파라미터들 (Developers 참고)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", getNaverClientId);
        params.add("client_secret", getNaverClientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(params, naverTokenRequestHeadres);

        // 서비스 서버에서 네이버 인증 서버로 요청 전송(POST 또는 GET이라고 공식문서에 있음), 응답은 Json으로 제공됨
        ResponseEntity<String> oauthTokenResponse = token_rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // body로 access_token, refresh_token, token_type:bearer, expires_in:3600 온 상태
        log.info(String.valueOf(oauthTokenResponse));

        // oauthTokenResponse로 받은 토큰정보 객체화
        ObjectMapper token_om = new ObjectMapper();
        NaverTokenVo naverToken = null;
        try {
            naverToken = token_om.readValue(oauthTokenResponse.getBody(), NaverTokenVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }


        // 토큰을 이용해 정보를 받아올 API 요청을 보낼 로직 작성하기
        RestTemplate profile_rt = new RestTemplate();
        HttpHeaders userDetailReqHeaders = new HttpHeaders();
        userDetailReqHeaders.add("Authorization", "Bearer " + naverToken.getAccess_token());
        userDetailReqHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(userDetailReqHeaders);

        // 서비스서버 - 네이버 인증서버 : 유저 정보 받아오는 API 요청
        ResponseEntity<String> userDetailResponse = profile_rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );

        // 요청 응답 확인
        log.info(String.valueOf(userDetailResponse));

        // 네이버로부터 받은 정보를 객체화
        // *이때, 공식문서에는 응답 파라미터에 mobile 밖에없지만, 국제전화 표기로 된 mobile_e164도 같이 옴. 따라서 NaverProfileVo에 mobile_e164 필드도 있어야 정상적으로 객체가 생성됨
        ObjectMapper profile_om = new ObjectMapper();
        NaverProfileVo naverProfile = null;
        try {
            naverProfile = profile_om.readValue(userDetailResponse.getBody(), NaverProfileVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }
        log.info(naverProfile.toString());
        log.info("hi ");

        Member member = memberService.findByEmail(naverProfile.getResponse().getEmail());

        log.info("저장됐는데.. ?");
        member = dtoToEntity(naverProfile);
        memberService.joinMember(member);
        log.info("해치웠나?");
        return member.getEmail();
    }
    @Transactional
    public TokenInfo tokenInfo(String memberEmail) {
        Member member = memberService.findByEmail(memberEmail);
        log.info("t0");
        UsernamePasswordAuthenticationToken authenticationToken
                //randPassword는 당연히 수정해야하는 문제..
                = new UsernamePasswordAuthenticationToken(member.getEmail(), "randPassword");
        log.info(String.valueOf(member.getId()));
        log.info("t1");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("t2");
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        log.info("t3");
        redisTemplate.opsForValue().set(
                "JWT_TOKEN:" + member.getEmail(), tokenInfo.getAccessToken());
        log.info("t4");
        return tokenInfo;
    }

    private Member dtoToEntity(NaverProfileVo vo){
        NaverProfileVo.response response = vo.getResponse();
        String birthday = response.getBirthday();
        String birthyear = response.getBirthyear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        // 연도와 생일을 결합하여 전체 날짜 문자열 생성
        String fullDate = birthday + "-" + birthyear;
        // 문자열을 LocalDate 객체로 변환
        LocalDate localDate = LocalDate.parse(fullDate, formatter);

//        String randPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        return Member.builder()
                .email(response.getEmail())
                .name(response.getName())
                .phoneNumber(response.getMobile())
                .password(passwordEncoder.encode("randPassword"))
                .birthDate(localDate)
                .build();


    }


}
