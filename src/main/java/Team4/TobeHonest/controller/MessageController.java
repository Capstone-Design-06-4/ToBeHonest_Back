package Team4.TobeHonest.controller;


import Team4.TobeHonest.dto.message.MessageResponseDTO;
import Team4.TobeHonest.dto.message.celebrate.SendCelebrateMessage;
import Team4.TobeHonest.dto.message.thanks.ThanksMessageDTO;
import Team4.TobeHonest.dto.message.thanks.ThanksWithNoImg;
import Team4.TobeHonest.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @PostMapping(value = "/send-thanks" ,consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    //@RequestBodt ==> only 1개 mapping  ==> @RequestPart로 나누기
    public ResponseEntity<String> sendThanksMessage(@RequestPart String requestJson,
                                              @RequestPart(required = false) List<MultipartFile> images,
                                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ThanksWithNoImg request = objectMapper.readValue(requestJson, ThanksWithNoImg.class);

        String userEmail = userDetails.getUsername();
        ThanksMessageDTO sendMessage = ThanksMessageDTO.builder()
                .wishItemId(request.getWishItemId())
                .title(request.getTitle())
                .contents(request.getContents())
                .messageType(request.getMessageType())
                .build();


        sendMessage.setImages(images);


        messageService.sendThanksMessageToAllContributor(sendMessage, userEmail);


        return ResponseEntity.ok("감사 메세지 전송 완료");
    }

    @PostMapping(value = "/send-celebrate")
    //@RequestBodt ==> only 1개 mapping  ==> @RequestPart로 나누기
    public ResponseEntity<String> sendMessageToAllContributor(@RequestBody SendCelebrateMessage request,
                                                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        String userEmail = userDetails.getUsername();
        messageService.sendCelebrateMessage(request, userEmail);

        return ResponseEntity.ok("메세지 전송 완료");
    }


    //친구랑 주고 받은 메시지를 return 해주는 controller


    @GetMapping("/find/wish-item/{wishItemId}")
    public List<MessageResponseDTO> findMessageWithWishItem(@AuthenticationPrincipal UserDetails userDetails,
                                                            @PathVariable Long wishItemId) {

        String loginEmail = userDetails.getUsername();
        return messageService.findMessageWithWishItemId(wishItemId, loginEmail);

    }

    @GetMapping("/find/friend-id/{friendId}")
    public List<MessageResponseDTO> findMessageWithFriendId(@AuthenticationPrincipal UserDetails userDetails,
                                                            @PathVariable Long friendId) {

        String loginEmail = userDetails.getUsername();
        return messageService.findMessageWithFriendId(friendId, loginEmail);

    }

}
