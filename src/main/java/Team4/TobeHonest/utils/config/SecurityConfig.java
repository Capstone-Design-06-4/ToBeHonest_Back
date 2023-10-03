
package Team4.TobeHonest.utils.config;


import Team4.TobeHonest.utils.handler.CustomLogoutHandler;
import Team4.TobeHonest.utils.handler.LoginFailHandler;
import Team4.TobeHonest.utils.handler.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity//모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션이다.
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {

        http.
            csrf().
                disable()
            .authorizeRequests()
                .requestMatchers("/login", "/signup", "/test").permitAll()
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .permitAll()
                .successHandler(new LoginSuccessHandler())
                .failureHandler(new LoginFailHandler());

//      로그아웃은 기본이 post방식으로..
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new CustomLogoutHandler());

        return http.build();
    }
}
