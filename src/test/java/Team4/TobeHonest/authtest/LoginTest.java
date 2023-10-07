package Team4.TobeHonest.authtest;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.service.MemberService;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    MemberService memberService;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        String email = "alswns2631@gmail.com";
        String name = "choiminjun";
        String passWord = "passw123";
        String phoneNumber = "010-1234-1231";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        Member member = joinDTO.toMember();
        memberService.join(joinDTO);
        //회원가입된 상태..
    }


    @Test
    @DisplayName("정상로그인")
    public void loginTest() throws Exception {
        //정상 로그인
        String email = "alswns2631@gmail.com";
        String passWord = "passw123";

        mockMvc.perform(formLogin().loginProcessingUrl("/login").user(email).password(passWord)).
                andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/members/information"));
    }

    @Test
    @DisplayName("해당 계정이 없는 경우")
    public void errorLoginTest() throws Exception {
        //정상 로그인
        String email = "alswns2631@gmail.com";
        String passWord = "pass23123";

        mockMvc.perform(formLogin().loginProcessingUrl("/login").user(email).password(passWord)).
                andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login")).andDo(print());
    }


    @Test
    @DisplayName("로그아웃")
    public void loginOutTest() throws Exception {
        //정상 로그인
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection()) // redirect 발생
                .andExpect(redirectedUrl("/login"))
                .andExpect(unauthenticated());


    }


}
