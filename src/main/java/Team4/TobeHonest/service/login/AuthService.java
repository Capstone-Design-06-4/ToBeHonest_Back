package Team4.TobeHonest.service.login;

import Team4.TobeHonest.exception.NoMemberException;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.utils.jwt.JwtTokenProvider;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;
    //message 전송하기..

    @Transactional
    public TokenInfo login(String memberId, String password) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue().set(
                "JWT_TOKEN:" + memberId, tokenInfo.getAccessToken());

        return tokenInfo;
    }

    @Transactional
    public String logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info(userEmail);
        //Token에서 로그인한 사용자 정보 get해 로그아웃 처리
        if (redisTemplate.opsForValue().get("JWT_TOKEN:" + userEmail) != null) {
            redisTemplate.delete("JWT_TOKEN:" + userEmail); //Token 삭제
        }
        else {
            throw new NoMemberException();

        }

        return userEmail + "로그아웃";
    }




}