package Team4.TobeHonest.controller;

import Team4.TobeHonest.dto.signup.JoinDTO;
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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    MemberService memberService;
    @Autowired
    HttpServletRequest request;
    @Autowired
    ObjectMapper objectMapper;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;

    @Before
    public void setUp() throws Exception {
        String email = "alswns2631@cau.ac.kr";
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
        memberService.join(joinDTO);

    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    @DisplayName("중복회원가입")
    public void errorJoin() throws Exception {
//        given


    }


}