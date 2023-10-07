package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
public class HomeControllerTest {
    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    WebApplicationContext context;
/*    @LocalServerPort
    private int port;*/
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {
        String email = "alswns2631@gmail.com";
        String name = "choiminjun";
        String passWord = "passw123";
        String phoneNumber = "010-1234-1231";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        Member member = joinDTO.toMember();
        memberService.join(joinDTO);
        //회원가입된 상태..
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @DisplayName("이메일에러")
    public void emailTest() throws Exception {
        String email = "alswns2631@gmail.com";
        String name = "마늘오리";
        String passWord = "passw123";
        String phoneNumber = "010-5421-1123";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        joinDTO.setEmail("alswns2631");
        String json = objectMapper.writeValueAsString(joinDTO);
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest()).andDo(print());
    }


    @Test
    @DisplayName("이름초과")
    public void nameTest() throws Exception {
        String email = "alswns2631@gmail.com";
        String name = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String passWord = "passw123";
        String phoneNumber = "010-5421-1123";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();

        String json = objectMapper.writeValueAsString(joinDTO);
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest()).andDo(print());
    }

    @Test
    @DisplayName("폰번호 타입 에러")
    public void testPhoneNumber() throws Exception {
        String email = "alswns2631@gmail.com";
        String name = "마늘오리";
        String passWord = "passw123";
        String phoneNumber = "01054211123";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        String json = objectMapper.writeValueAsString(joinDTO);
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest()).andDo(print());
    }


    @Test
    @DisplayName("중복 회원가입")
    public void duplicateSignUp() throws Exception {

        String email = "alswns2631@gmail.com";
        String name = "마늘오리";
        String passWord = "passw123";
        String phoneNumber = "010-5421-1123";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();

        String json = objectMapper.writeValueAsString(joinDTO);
        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isConflict()).andDo(print());

    }

    @Test
    @DisplayName("정상회원가입")
    public void join() throws Exception {
        String email = "alswns2631@cau.ac.kr";
        String name = "helloThere";
        String passWord = "pa1ssw123";
        String phoneNumber = "010-1214-1234";
        String birthDate = "20000803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        String json = objectMapper.writeValueAsString(joinDTO);


        mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andDo(print());

    }


}