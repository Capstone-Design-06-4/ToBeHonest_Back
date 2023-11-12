package Team4.TobeHonest.authtest;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.dto.signup.LoginDTO;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    MemberService memberService;
    @Autowired
    ObjectMapper objectMapper;
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
        LoginDTO login = LoginDTO.builder().email(email).password(passWord).build();
        String s = objectMapper.writeValueAsString(login);
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(s)).
                andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("해당 계정이 없는 경우")
    public void errorLoginTest() throws Exception {
        //정상 로그인
        String email = "alswns2631@gmail.com";
        String passWord = "pass23123";

        LoginDTO login = LoginDTO.builder().email(email).password(passWord).build();
        String s = objectMapper.writeValueAsString(login);
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(s)).
                andDo(print())
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("로그아웃")
    public void loginOutTest() throws Exception {
        //정상 로그인
        String email = "alswns2631@gmail.com";
        String passWord = "passw123";
        LoginDTO login = LoginDTO.builder().email(email).password(passWord).build();
        String s = objectMapper.writeValueAsString(login);
        MvcResult mvcResult = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(s)).
                andDo(print())
                .andExpect(status().isOk()).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        TokenInfo tokenInfo = objectMapper.readValue(responseBody, TokenInfo.class);

        //access토큰이 정상적으로 expire됐는가?

        String accessToken = tokenInfo.getAccessToken();
        mockMvc.perform(post("/Logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());


        mockMvc.perform(get("/findEmail")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("이미 로그인 된 상태에서 다른 사람이 로그인하려고 들때.")
    public void duplicate_login() throws Exception {

        //한명이 로그인 한 상황..
        String email = "alswns2631@gmail.com";
        String passWord = "passw123";
        LoginDTO login = LoginDTO.builder().email(email).password(passWord).build();
        String s = objectMapper.writeValueAsString(login);
        MvcResult mvcResult = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(s))
                .andExpect(status().isOk()).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        TokenInfo tokenInfo1 = objectMapper.readValue(responseBody, TokenInfo.class);


        //그 뒤 다른 놈이 같은 계정으로 로그인했다

        login = LoginDTO.builder().email(email).password(passWord).build();
        s = objectMapper.writeValueAsString(login);
        mvcResult = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(s))
                .andExpect(status().isOk()).andReturn();

        responseBody = mvcResult.getResponse().getContentAsString();



        TokenInfo tokenInfo2 = objectMapper.readValue(responseBody, TokenInfo.class);
        String accessToken = tokenInfo1.getAccessToken();
        System.out.println(tokenInfo1.getAccessToken());
        System.out.println(tokenInfo2.getAccessToken());
        mockMvc.perform(get("/findEmail")
                        .header("Authorization", "Bearer " + tokenInfo1.getAccessToken()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/findEmail")
                        .header("Authorization", "Bearer " + tokenInfo2.getAccessToken()))
                .andExpect(status().isOk());


    }


}
