package Team4.TobeHonest.service;

import Team4.TobeHonest.dto.contributor.ContributorDTO;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import Team4.TobeHonest.dto.message.celebrate.SendCelebrateMessage;
import Team4.TobeHonest.dto.message.thanks.ThanksMessageDTO;
import Team4.TobeHonest.enumer.MessageType;
import Team4.TobeHonest.setup.FriendJoinService;
import Team4.TobeHonest.setup.NaverSearchService;
import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.repo.*;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Autowired
    public ImageService imageService;

    @Autowired
    public ContributorService contributorService;
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
        friend1.addPoints(100000000);
        friend2.addPoints(100000000);
        //위시아이템에 추가
        wishItemService.addWishList(this.member, DTO);
        DTO = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(1));
        wishItemService.addWishList(this.friend1, DTO);
        WishItem wishItem = wishItemRepository.findAll(member).get(0);
        contributorService.contributing(friend1, wishItem.getId(), (Integer)(this.galaxy.get(0).getPrice() / 3) + 1);
        contributorService.contributing(friend2, wishItem.getId(), (Integer)(this.galaxy.get(0).getPrice() / 3) * 2 );

    }


    @Test
    @DisplayName("축하 메시지 보내기..")
    public void sendCelebrateMessageTest() throws IOException {
        WishItem wishItem = wishItemRepository.findAll(member).get(0);
        SendCelebrateMessage celebrateMessage1 = SendCelebrateMessage.builder()
                .wishItemId(wishItem.getId())
                .title("돈갚으라 1")
                .contents("돈갚으라고 1")
                .messageType(MessageType.CELEBRATION_MSG).build();

        messageService.sendCelebrateMessage(celebrateMessage1, friend1.getEmail());


        SendCelebrateMessage celebrateMessage2 = SendCelebrateMessage.builder()
                .wishItemId(wishItem.getId())
                .title("돈갚으라 2")
                .contents("돈갚으라고 2")
                .messageType(MessageType.CELEBRATION_MSG).build();

        messageService.sendCelebrateMessage(celebrateMessage2, friend2.getEmail());

        List<MessageResponseDTO> messageResponse = messageRepository.msgWithWithWishItem(wishItem.getId());

        List<Long> senderList = messageResponse.stream().map(MessageResponseDTO::getSenderId).toList();
        List<Long> itemList = messageResponse.stream().map(MessageResponseDTO::getItemId).toList();
        
        
        //2개가 맞는가
        Assertions.assertThat(messageResponse.size()).isEqualTo(2);
        //원하는 위시아이템에 보내줬는가
        Assertions.assertThat(messageResponse.get(0).getWishItemId()).isEqualTo(wishItem.getId());
        Assertions.assertThat(messageResponse.get(1).getWishItemId()).isEqualTo(wishItem.getId());
        
        //보낸사람이 잘 저장됐는가
        Assertions.assertThat(senderList).contains(friend1.getId());
        Assertions.assertThat(senderList).contains(friend2.getId());
        
        //아이템원하는 아이템정보를 포함하는가
        Assertions.assertThat(itemList).contains(this.galaxy.get(0).getId());

        
        //축하 메시지가 맞는가
        Assertions.assertThat(messageResponse.get(0).getMessageType()).isEqualTo(MessageType.CELEBRATION_MSG);
        Assertions.assertThat(messageResponse.get(1).getMessageType()).isEqualTo(MessageType.CELEBRATION_MSG);
        





    }


    @Test
    @DisplayName("감사 메시지 조회하기..")
    public void testFindMessageWithFriend(){

        MultipartFile multipartFile = convertUrlToMultipartFile(galaxy.get(0).getImage());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);
        multipartFile = convertUrlToMultipartFile(galaxy.get(1).getImage());
        list.add(multipartFile);
        WishItem wishItem = wishItemRepository.findAll(member).get(0);

        //friend1 -> member
        ThanksMessageDTO thanksMessageDTO1 = ThanksMessageDTO.builder()
                .title("ㄳ1").contents("ㄳ1").images(list)
                .messageType(MessageType.THANKS_MSG)
                .wishItemId(wishItem.getId())
                .build();


        //감사메시지 전송하기..
        List<ContributorDTO> contributor = contributorService.findContributor(wishItem.getId());
        System.out.println(contributor.size());
        messageService.sendThanksMessageToAllContributor(thanksMessageDTO1, member.getEmail());


        List<MessageResponseDTO> messageResponse = messageRepository.msgWithWithWishItem(wishItem.getId());
        System.out.println(messageResponse.size());
        List<Long> senderList = messageResponse.stream().map(MessageResponseDTO::getSenderId).toList();
        List<Long> itemList = messageResponse.stream().map(MessageResponseDTO::getItemId).toList();


        //2개가 맞는가
        Assertions.assertThat(messageResponse.size()).isEqualTo(2);
        //원하는 위시아이템에 보내줬는가
        Assertions.assertThat(messageResponse.get(0).getWishItemId()).isEqualTo(wishItem.getId());
        Assertions.assertThat(messageResponse.get(1).getWishItemId()).isEqualTo(wishItem.getId());

        //보낸사람이 잘 저장됐는가
        Assertions.assertThat(senderList).contains(member.getId());
        Assertions.assertThat(senderList).doesNotContain(friend2.getId());
        Assertions.assertThat(senderList).doesNotContain(friend1.getId());


        //아이템원하는 아이템정보를 포함하는가
        Assertions.assertThat(itemList).contains(this.galaxy.get(0).getId());


        //감사 메시지가 맞는가
        Assertions.assertThat(messageResponse.get(0).getMessageType()).isEqualTo(MessageType.THANKS_MSG);
        Assertions.assertThat(messageResponse.get(1).getMessageType()).isEqualTo(MessageType.THANKS_MSG);



        //총 3개 보냄
    


    }


   /* @Test
    @DisplayName("contributor 전부에게 보내줬는지 확인하기..")
    void test_send_all_contributor(){

        ItemInfoDTO g1 = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        wishItemService.addWishList(this.member, g1);

        WishItem wishItem = wishItemRepository.findAll(member).get(0);

        contributorService.contributing(friend1,wishItem.getId(), 10000);
        contributorService.contributing(friend2,wishItem.getId(), 10000);

    }*/

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
