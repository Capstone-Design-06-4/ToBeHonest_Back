package Team4.TobeHonest.service.login.OauthLogin;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.login.NaverProfileVo;
import Team4.TobeHonest.dto.login.NaverTokenVo;
import Team4.TobeHonest.exception.NoMemberException;
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

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private String getNaverClientId = "Pi8zB3f4zuenOa7Lpdpl";
    private String getNaverClientSecret = "cB7nj_4ahS";


    public String createURL() throws UnsupportedEncodingException {
        StringBuffer url = new StringBuffer();

        // 카카오 API 명세에 맞춰서 작성
        String redirectURI = URLEncoder.encode("http://10.210.60.138:8080/oauth/naver-login", "UTF-8"); // redirectURI 설정 부분
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
    public String login(String accessToken) throws JsonProcessingException {

        // 토큰을 이용해 정보를 받아올 API 요청을 보낼 로직 작성하기
        RestTemplate profile_rt = new RestTemplate();
        HttpHeaders userDetailReqHeaders = new HttpHeaders();
        userDetailReqHeaders.add("Authorization", "Bearer " + accessToken);
        userDetailReqHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(userDetailReqHeaders);
        log.info(accessToken);
        // 서비스서버 - 네이버 인증서버 : 유저 정보 받아오는 API 요청
        ResponseEntity<String> userDetailResponse = profile_rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );
        log.info("good");
        // 요청 응답 확인

        // 네이버로부터 받은 정보를 객체화
        // *이때, 공식문서에는 응답 파라미터에 mobile 밖에없지만, 국제전화 표기로 된 mobile_e164도 같이 옴. 따라서 NaverProfileVo에 mobile_e164 필드도 있어야 정상적으로 객체가 생성됨
        ObjectMapper profile_om = new ObjectMapper();
        NaverProfileVo naverProfile = null;
        try {
            naverProfile = profile_om.readValue(userDetailResponse.getBody(), NaverProfileVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }
        Member member = memberService.findByEmailWithNoException(naverProfile.getResponse().getEmail());
        if (member == null){
            member = dtoToEntity(naverProfile);
            saveMemberifNotExist(member);
        }

        return member.getEmail();
    }

    private void saveMemberifNotExist(Member member){
        memberService.joinMember(member);
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

    private Member dtoToEntity(NaverProfileVo vo) {
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
