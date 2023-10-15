package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.Message;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.message.SendMessageDTO;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.repo.MessageRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import Team4.TobeHonest.repo.image.ImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final WishItemRepository wishItemRepository;
    private final ImageRepository imageRepository;
    private static final String IMAGE_UPLOAD_DIR = "images/";
    public void saveMessage(SendMessageDTO sendMessageDTO){

        //통신을 너무 많이 하는 듯..
        Member receiver = memberRepository.findById(sendMessageDTO.getReceiverId());
        Member sender = memberRepository.findById(sendMessageDTO.getSenderId());
        WishItem wishItem = wishItemRepository.findWishItemById(sendMessageDTO.getWishItemId());
        Message message = Message.builder()
                .receiver(receiver)
                .sender(sender)
                .relatedItem(wishItem)
                .title(sendMessageDTO.getTitle())
                .content(sendMessageDTO.getContents())
                .time(LocalDateTime.now())
                .build();

        //메시지 레포에 저장?
        messageRepository.join(message);
        //이미지 저장
        for (MultipartFile image : sendMessageDTO.getImages()) {
            String url = imageRepository.saveImg(image, message.getId());
            message.addImage(url);
        }



    }
}
