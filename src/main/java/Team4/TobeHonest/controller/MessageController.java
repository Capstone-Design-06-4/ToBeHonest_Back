package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import Team4.TobeHonest.dto.message.SendMessageDTO;
import Team4.TobeHonest.dto.message.SendMessageWithNoIMG;
import Team4.TobeHonest.exception.NoWishItemException;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final MemberService memberService;

    @PostMapping("/send")
    //@RequestBodt ==> only 1개 mapping  ==> @RequestPart로 나누기
    public ResponseEntity<String> sendMessage(@RequestPart SendMessageWithNoIMG sendMessageWithNoIMG,
                                              @RequestPart(required = false) List<MultipartFile> images,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        SendMessageDTO sendMessage = SendMessageDTO.builder()
                .senderId(sendMessageWithNoIMG.getSenderId())
                .receiverId(sendMessageWithNoIMG.getReceiverId())
                .wishItemId(sendMessageWithNoIMG.getWishItemId())
                .title(sendMessageWithNoIMG.getTitle())
                .contents(sendMessageWithNoIMG.getContents())
                .build();


        sendMessage.setImages(images);
        if (!sendMessage.getSenderId().equals(member.getId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("sender와 로그인 한 유저의 정보가 다릅니다");
        }
        messageService.sendMessage(sendMessage);

        return ResponseEntity.ok("메세지 전송 완료");
    }

    //친구랑 주고 받은 메시지를 return 해주는 controller
    @GetMapping("/find/{friendId}")
    public List<MessageResponseDTO> findMessageWithFriend(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable Long friendId){
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        return messageService.messageWithFriend(member.getId(), friendId);

    }

}
