package Team4.TobeHonest.utils.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        //사실 알아서 session 삭제해줘서 해줄필요는 없음..
        if (authentication != null && authentication.getDetails() != null) {
            try {
                request.getSession().invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("/login");
    }

}
