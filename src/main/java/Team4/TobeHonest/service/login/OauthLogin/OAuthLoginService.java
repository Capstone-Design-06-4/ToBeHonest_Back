package Team4.TobeHonest.service.login.OauthLogin;

import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

public interface OAuthLoginService {

    //최초 로그인하는 화면 URL return
    public String createURL() throws UnsupportedEncodingException;

    @Transactional
    public String login(String code, String state, HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException;

    @Transactional
    public TokenInfo tokenInfo(String memberEmail);
}
