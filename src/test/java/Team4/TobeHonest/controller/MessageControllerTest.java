package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.message.SendMessageDTO;
import Team4.TobeHonest.dto.message.SendMessageWithNoIMG;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import Team4.TobeHonest.service.FriendService;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.MessageService;
import Team4.TobeHonest.service.WishItemService;
import Team4.TobeHonest.setup.FriendJoinService;
import Team4.TobeHonest.setup.NaverSearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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

    @MockBean
    private UserDetails userDetails;

    public Member member;
    public Member friend1;
    public Member friend2;
    public List<Item> galaxy;

    public WishItem wishItem;



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


        //member로 로그인한 상황..
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities()));

    }

    @Test
    @DisplayName("정상 메시지 전송")
    @WithMockUser
    //@WithMockUser 어노테이션을 사용하면 다음과 같이 간단하게 인증된 사용자를 만들지 않아도 테스트를 요청할 수 있다.
    public void sendMessageTest_Success() throws Exception {
        SendMessageDTO sendMessage = new SendMessageDTO();
        // 여기에 sendMessage 객체를 초기화하는 코드 추가
        String filePath1 = "C:\\Users\\alswn\\TobeHonest\\src\\main\\resources\\profile\\default.jpeg";
        String filePath2 = "C:\\Users\\alswn\\TobeHonest\\src\\main\\resources\\messages\\1\\1.jpg";

        Path path1 = Paths.get(filePath1);
        byte[] content1 = Files.readAllBytes(path1);
        MockMultipartFile file1 = new MockMultipartFile("images", "default.jpeg", "image/jpeg", content1);

        Path path2 = Paths.get(filePath2);
        byte[] content2 = Files.readAllBytes(path2);
        MockMultipartFile file2 = new MockMultipartFile("images", "1.jpg", "image/jpg", content2);

        SendMessageWithNoIMG build = SendMessageWithNoIMG.builder()
                .senderId(member.getId())
                .receiverId(friend1.getId())
                .wishItemId(wishItem.getId())
                .title("메롱")
                .contents("ㅗ").build();
        String json = objectMapper.writeValueAsString(build);
        MockMultipartFile sendMessageWithNoIMGPart =
                new MockMultipartFile("sendMessageWithNoIMG", "", "application/json",
                json.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/message/send")
                        .file(sendMessageWithNoIMGPart)
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


    //위에서 이미지 전송 잘되는 것을 확인 했으니.. pass
    @Test
    @DisplayName("로그인 유저와 sender의 정보가 다른 경우..")
    @WithMockUser
    public void sendMessageTest_NonAuthorized() throws Exception{
        SendMessageWithNoIMG build = SendMessageWithNoIMG.builder()
                .senderId(friend2.getId())
                .receiverId(friend1.getId())
                .wishItemId(wishItem.getId())
                .title("메롱")
                .contents("ㅗ").build();
        String json = objectMapper.writeValueAsString(build);
        MockMultipartFile sendMessageWithNoIMGPart =
                new MockMultipartFile("sendMessageWithNoIMG", "", "application/json",
                        json.getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart("/message/send")
                        .file(sendMessageWithNoIMGPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("메시지 주고받았을 때, 해방 정보를 return해주기 ")
    @WithMockUser
    public void sendReceive_Check(){

    }





}
