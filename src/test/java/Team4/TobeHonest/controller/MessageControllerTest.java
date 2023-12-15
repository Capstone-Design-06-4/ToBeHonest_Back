package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.message.SendMessageWithNoIMG;
import Team4.TobeHonest.dto.message.celebrate.SendCelebrateMessage;
import Team4.TobeHonest.dto.message.thanks.ThanksMessageDTO;
import Team4.TobeHonest.dto.message.thanks.ThanksWithNoImg;
import Team4.TobeHonest.dto.signup.LoginDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.enumer.MessageType;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import Team4.TobeHonest.service.*;
import Team4.TobeHonest.setup.FriendJoinService;
import Team4.TobeHonest.setup.NaverSearchService;
import Team4.TobeHonest.utils.jwt.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
@AutoConfigureMockMvc
public class MessageControllerTest {

    @Autowired
    FriendJoinService friendJoinService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    FriendService friendService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MessageService messageService;
    @Autowired
    NaverSearchService naverSearchService;
    @Autowired
    WishItemService wishItemService;
    @Autowired
    WishItemRepository wishItemRepository;

    @Autowired
    ContributorService contributorService;
    MockHttpSession mockSession = new MockHttpSession();
    @Autowired
    HttpServletRequest request;

    public Member member;
    public Member friend1;
    public Member friend2;
    public List<Item> galaxy;

    public WishItem wishItem;
    public String accessToken;



    @Before
    public void setUp() throws Exception {
        naverSearchService.saveItemInDB();
        friendJoinService.saveMemberInDB();
        this.galaxy = itemRepository.searchItemName("갤럭시");
        this.member = memberService.findByEmail("alswns2631@cau.ac.kr");
        this.friend1 = memberService.findByEmail("alswns2631@gmail.com");
        this.friend2 = memberService.findByEmail("alswns2631@naver.com");
        friendService.addFriendList(member, friend1);
        friendService.addFriendList(member, friend2);
        friendService.addFriendList(friend1, member);
        friendService.addFriendList(friend2, member);
        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        wishItemService.addWishList(this.member, DTO);
        wishItem = wishItemRepository.findAll(member).get(0);

        String email = "alswns2631@cau.ac.kr";
        String passWord = "passw123";
        LoginDTO login = LoginDTO.builder().email(email).password(passWord).build();
        String s = objectMapper.writeValueAsString(login);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/login").session(mockSession).contentType(MediaType.APPLICATION_JSON).content(s))
                .andExpect(status().isOk()).andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        TokenInfo tokenInfo = objectMapper.readValue(responseBody, TokenInfo.class);

        //access토큰이 정상적으로 expire됐는가?

        this.accessToken = tokenInfo.getAccessToken();
        friend1.addPoints(100000000);
        friend2.addPoints(100000000);
        contributorService.contributing(friend1, wishItem.getId(), (Integer)(wishItem.getItem().getPrice() / 3) + 3);
        contributorService.contributing(friend2, wishItem.getId(), ((Integer)(wishItem.getItem().getPrice() / 3)) * 2);




    }



    //위에서 이미지 전송 잘되는 것을 확인 했으니.. pass
    @Test
    @DisplayName("축하 메시지 전달")
    @WithMockUser
    public void sendContirbuteMessage() throws Exception {



        SendCelebrateMessage dto = SendCelebrateMessage.builder()
                .messageType(MessageType.CELEBRATION_MSG)
                .wishItemId(wishItem.getId())
                .title("돈갚아라1")
                .contents("돈갚아라1")
                .build();


        String body = objectMapper.writeValueAsString(dto);
        mockMvc.perform(MockMvcRequestBuilders.post("/message/send-celebrate")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(body)

        ).andExpect(status().isOk()).andDo(print());


    }

    @Test
    @DisplayName("감사 메시지 전달")
    @WithMockUser
    public void sendThanksMessage() throws Exception {

        String filePath1 = "C:\\Users\\alswn\\TobeHonest\\src\\main\\resources\\profile\\default.jpeg";
        String filePath2 = "C:\\Users\\alswn\\TobeHonest\\src\\main\\resources\\messages\\1\\1.jpg";

        Path path1 = Paths.get(filePath1);
        byte[] content1 = Files.readAllBytes(path1);
        MockMultipartFile file1 = new MockMultipartFile("images", "default.jpeg", "image/jpeg", content1);

        Path path2 = Paths.get(filePath2);
        byte[] content2 = Files.readAllBytes(path2);
        MockMultipartFile file2 = new MockMultipartFile("images", "1.jpg", "image/jpg", content2);

        ThanksWithNoImg dto = ThanksWithNoImg.builder()
                .wishItemId(wishItem.getId())
                .title("돈안갚냐?")
                .contents("돈갚아라고")
                .messageType(MessageType.THANKS_MSG)
                .build();
        String json = objectMapper.writeValueAsString(dto);
        MockMultipartFile sendMessageWithNoIMGPart =
                new MockMultipartFile("requestJson", "", "application/json",
                        json.getBytes(StandardCharsets.UTF_8));


        mockMvc.perform(multipart("/message/send-thanks")
                        .file(sendMessageWithNoIMGPart)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA).header("Authorization", "Bearer " + accessToken)
                        .session(mockSession))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());


    }




}
