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

    @Transactional
    public String login(String accessToken) throws JsonProcessingException {
        if (accessToken.isEmpty()){
            throw new RuntimeException("No Token");

        }
        log.info("test1");
        log.info("{} accessToken", accessToken);
        // 토큰을 이용해 정보를 받아올 API 요청을 보낼 로직 작성하기
        RestTemplate profile_rt = new RestTemplate();
        HttpHeaders userDetailReqHeaders = new HttpHeaders();
        userDetailReqHeaders.add("Authorization", "Bearer " + accessToken);
        userDetailReqHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(userDetailReqHeaders);
        log.info("test2");
        // 서비스서버 - 네이버 인증서버 : 유저 정보 받아오는 API 요청
        ResponseEntity<String> userDetailResponse = profile_rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );
        // 요청 응답 확인
        log.info("test3");

        // 네이버로부터 받은 정보를 객체화
        // *이때, 공식문서에는 응답 파라미터에 mobile 밖에없지만, 국제전화 표기로 된 mobile_e164도 같이 옴. 따라서 NaverProfileVo에 mobile_e164 필드도 있어야 정상적으로 객체가 생성됨
        ObjectMapper profile_om = new ObjectMapper();
        NaverProfileVo naverProfile = null;
        try {
            naverProfile = profile_om.readValue(userDetailResponse.getBody(), NaverProfileVo.class);
        } catch (JsonMappingException je) {
            je.printStackTrace();
        }
        log.info("test4");

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
