package Team4.TobeHonest.service;

import Team4.TobeHonest.dto.message.MessageResponseDTO;
import Team4.TobeHonest.setup.FriendJoinService;
import Team4.TobeHonest.setup.NaverSearchService;
import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.ApiItemDTO;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.message.SendMessageDTO;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.repo.*;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
public class MessageServiceTest {

    @Autowired
    NaverSearchService naverSearchService;
    @Autowired
    FriendJoinService friendJoinService;

    @Autowired
    public WishItemService wishItemService;
    @Autowired
    public WishItemRepository wishItemRepository;

    @Autowired
    public MessageService messageService;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public ItemRepository itemRepository;
    @Autowired
    public MemberService memberService;
    public Member member;
    public Member friend1;
    public Member friend2;
    public List<Item> galaxy;


    @Before
    public void setUp() throws Exception {
        naverSearchService.saveItemInDB();
        friendJoinService.saveMemberInDB();
        this.galaxy = itemRepository.searchItemName("갤럭시");
        this.member = memberService.findByEmail("alswns2631@cau.ac.kr");
        this.friend1 = memberService.findByEmail("alswns2631@gmail.com");
        this.friend2 = memberService.findByEmail("alswns2631@naver.com");
        //위시아이템 설정..
        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        //위시아이템에 추가
        wishItemService.addWishList(this.member, DTO);
        DTO = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(1));
        wishItemService.addWishList(this.friend1, DTO);
    }


    @Test
    @DisplayName("메시지 보내기..")
    public void sendMessageTest(){

        MultipartFile multipartFile = convertUrlToMultipartFile(galaxy.get(0).getImage());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);
        multipartFile = convertUrlToMultipartFile(galaxy.get(1).getImage());
        list.add(multipartFile);
        WishItem wishItem = wishItemRepository.findAll(member).get(0);
        SendMessageDTO sendMessageDTO = SendMessageDTO.builder()
                .title("돈갚아라").contents("돈갚으라고").images(list)
                .receiverId(member.getId()).senderId(friend1.getId())
                .wishItemId(wishItem.getId())
                .build();
        messageService.sendMessage(sendMessageDTO);
        List<Message> all = messageRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(1);
        Message message = all.get(0);

        Assertions.assertThat(message.getRelatedItem().getId())
                .isEqualTo(wishItem.getId());
        //2개다 저장됐는가..
        Assertions.assertThat(message.getMessageImgList().size()).isEqualTo(2);





    }


    @Test
    @DisplayName("친구랑 주고받은 메시지 조회하기..")
    public void testFindMessageWithFriend(){

        MultipartFile multipartFile = convertUrlToMultipartFile(galaxy.get(0).getImage());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);
        multipartFile = convertUrlToMultipartFile(galaxy.get(1).getImage());
        list.add(multipartFile);
        WishItem wishItem = wishItemRepository.findAll(member).get(0);

        //friend1 -> member
        SendMessageDTO sendMessageDTO1 = SendMessageDTO.builder()
                .title("돈갚아라").contents("돈갚으라고").images(list)
                .receiverId(member.getId()).senderId(friend1.getId())
                .wishItemId(wishItem.getId())
                .build();


        WishItem wishItem1 = wishItemRepository.findAll(friend1).get(0);
        //member -> friend1
        SendMessageDTO sendMessageDTO2 = SendMessageDTO.builder()
                .title("싫은뎅ㅋ").contents("내가... 왜").images(list)
                .receiverId(friend1.getId()).senderId(member.getId())
                .wishItemId(wishItem1.getId())
                .build();

        messageService.sendMessage(sendMessageDTO1);
        messageService.sendMessage(sendMessageDTO2);

        //friend1 -> member
        sendMessageDTO1 = SendMessageDTO.builder()
                .title("나쁘네").contents("ㅠㅠ").images(list)
                .receiverId(member.getId()).senderId(friend1.getId())
                .wishItemId(wishItem.getId())
                .build();

        messageService.sendMessage(sendMessageDTO1);
        List<MessageResponseDTO> messageResponseDTOS =
                messageService.messageWithFriend(member.getId(), friend1.getId());
        //총 3개 보냄
        Assertions.assertThat(messageResponseDTOS.size()).isEqualTo(3);
        //friend1 -> member
        //수신1개
        List<Message> myReceive = messageRepository.findMyReceive(member.getId(), friend1.getId());
        Message receive = myReceive.get(0);
        Assertions.assertThat(myReceive.size()).isEqualTo(2);
        Assertions.assertThat(receive.getReceiver()).isEqualTo(member);
        Assertions.assertThat(receive.getSender()).isEqualTo(friend1);
        Assertions.assertThat(receive.getRelatedItem()).isEqualTo(wishItem);

        //발신 2개
        List<Message> mySent = messageRepository.findMySent(member.getId(), friend1.getId());
        Assertions.assertThat(mySent.size()).isEqualTo(1);

        //대칭test
        myReceive = messageRepository.findMyReceive(friend1.getId(), member.getId());
        Assertions.assertThat(myReceive.size()).isEqualTo(1);

        mySent = messageRepository.findMySent(friend1.getId(), member.getId());
        Assertions.assertThat(mySent.size()).isEqualTo(2);



    }


    private MultipartFile convertUrlToMultipartFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();

            // Extract the file name from the URL
            String[] parts = fileUrl.split("/");
            String originalFilename = parts[parts.length-1];
            String contentType = httpURLConnection.getContentType(); // this might not always be accurate

            // Convert InputStream to MultipartFile using MockMultipartFile
            MultipartFile multipartFile = new MockMultipartFile(originalFilename, originalFilename, contentType, inputStream);

            return multipartFile;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert URL to MultipartFile: " + e.getMessage());
        }
    }






}
