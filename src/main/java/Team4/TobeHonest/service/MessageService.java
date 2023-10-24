package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.Message;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import Team4.TobeHonest.dto.message.SendMessageDTO;
import Team4.TobeHonest.exception.NoWishItemException;
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
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class MessageService {

    private static final String IMAGE_UPLOAD_DIR = "images/messages";
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final WishItemRepository wishItemRepository;
    private final ImageRepository imageRepository;

    //message 전송하기..
    @Transactional
    public void sendMessage(SendMessageDTO sendMessageDTO) {
        //통신을 너무 많이 하는 듯..
        Member receiver = memberRepository.findById(sendMessageDTO.getReceiverId());
        Member sender = memberRepository.findById(sendMessageDTO.getSenderId());
        WishItem wishItem = wishItemRepository.findWishItemById(sendMessageDTO.getWishItemId());
        if (wishItem == null){
            throw new NoWishItemException();
        }
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

    public List<MessageResponseDTO> messageWithFriend(Long memberId, Long friendId){
        return messageRepository.msgWithMyFriend(memberId, friendId);
    }

}
