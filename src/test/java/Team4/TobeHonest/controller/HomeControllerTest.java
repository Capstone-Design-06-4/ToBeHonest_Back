package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.JoinDTO;
import Team4.TobeHonest.dto.LoginDTO;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
public class HomeControllerTest {
    @Autowired
    MemberService memberService;
    @Autowired
    HttpServletRequest request;
    @Autowired
    ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;

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
        memberService.join(member);
        //회원가입된 상태..
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @DisplayName("post /join, ==> 중복이메일 회원")
    public void errorJoin() throws Exception {
//        given
//        create table이라 yml파일 수정할 경우 test가 안돌아 갈 수 있음..
        String email = "alswns2631@gmail.com";
        String name = "마늘오리";
        String passWord = "passw123";
        String phoneNumber = "010-5421-1123";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();

        String url = "http://localhost:" + port + "/join";
        String json = objectMapper.writeValueAsString(joinDTO);
//        when
        System.out.println(joinDTO);
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isConflict());

    }

    @Test
    @DisplayName("post /join, 정상회원가입")
    public void join() throws Exception {
        String email = "alswns2631@cau.ac.kr";
        String name = "helloThere";
        String passWord = "pa1ssw123";
        String phoneNumber = "010-1214-1234";
        String birthDate = "20000803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        String url = "http://localhost:" + port + "/join";
        String json = objectMapper.writeValueAsString(joinDTO);

//        when
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());


    }



    @Test
    public void loginAndOutTest() throws Exception {
        //정상 로그인
        String email = "alswns2631@gmail.com";
        String passWord = "passw123";
        LoginDTO loginDTO = LoginDTO.builder().email(email).passWord(passWord).build();
        String url = "http://localhost:" + port + "/login";

        String json = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(post(url).
                contentType(MediaType.APPLICATION_JSON).
                content(json)
        ).andExpect(status().isOk()).andExpect(cookie().exists("sessionId"));

        url = "http://localhost:" + port + "/logout";
        mockMvc.perform(post(url)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/login"));
    }

}