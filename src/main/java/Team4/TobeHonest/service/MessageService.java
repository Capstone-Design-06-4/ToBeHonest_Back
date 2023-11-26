package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.Message;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.contributor.ContributorDTO;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import Team4.TobeHonest.dto.message.celebrate.SendCelebrateMessage;
import Team4.TobeHonest.dto.message.thanks.ThanksMessageDTO;
import Team4.TobeHonest.exception.NoMemberException;
import Team4.TobeHonest.exception.NoWishItemException;
import Team4.TobeHonest.exception.NotValidWishItemException;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.repo.MessageRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ContributorRepository contributorRepository;
    //message 전송하기..


    public List<MessageResponseDTO> findMessageWithFriendId(Long friendId, String userEmail) {


        Member member = memberRepository.findByEmail(userEmail);

        if (memberRepository.findById(friendId) == null){
            throw new NoMemberException("friend가 존재하지 않습니다");
        }
        return messageRepository.msgWithMyFriend(member.getId(), friendId);
    }


    public List<MessageResponseDTO> findMessageWithWishItemId(Long wishItemId, String userEmail){
        WishItem wishItem = wishItemRepository.findWishItemById(wishItemId);
        if (wishItem == null){
            throw new NoWishItemException("wishItem이 존재하지 않습니다");
        }

        if(!wishItem.getMember().getEmail().equals(userEmail)){
            throw new NotValidWishItemException("접근권한이 없습니다");
        }
        return messageRepository.msgWithWithWishItem(wishItemId);
    }


    @Transactional
    public void sendThanksMessageToAllContributor(ThanksMessageDTO sendMessageDTO, String memberEmail) {

        //통신을 너무 많이 하는 듯..

        Member sender = memberRepository.findByEmail(memberEmail);

        WishItem wishItem = wishItemRepository.findWishItemById(sendMessageDTO.getWishItemId());

        if (wishItem == null) {
            throw new NoWishItemException();
        }
        //메시지 보낸걸로 바꾸기
        wishItem.changeIsThanksMessagedSend();

        List<ContributorDTO> contributorsInWishItem = contributorRepository.findContributorsInWishItem(wishItem.getId());
        List<Long> allContributors = contributorsInWishItem.stream().map(ContributorDTO::getFriendId).toList();
        List<Message> messages = allContributors.stream().map(e -> Message.builder()
                .receiver(memberRepository.findById(e))
                .sender(sender)
                .relatedItem(wishItem)
                .title(sendMessageDTO.getTitle())
                .content(sendMessageDTO.getContents())
                .time(LocalDateTime.now())
                .messageType(sendMessageDTO.getMessageType())
                .fundMoney(contributorRepository.findFundedAmount(wishItem, memberRepository.findById(e)))
                .build()).toList();

        messageRepository.saveAll(messages);
        //이미지 저장

    }



    @Transactional
    public void sendCelebrateMessage(SendCelebrateMessage celebrateMessage, String memberEmail) {
        //통신을 너무 많이 하는 듯..

        Member sender = memberRepository.findByEmail(memberEmail);


        WishItem wishItem = wishItemRepository.findWishItemById(celebrateMessage.getWishItemId());

        if (wishItem == null) {
            throw new NoWishItemException();
        }


        Message message = Message.builder()
                .receiver(wishItem.getMember())
                .sender(sender)
                .relatedItem(wishItem)
                .title(celebrateMessage.getTitle())
                .content(celebrateMessage.getContents())
                .time(LocalDateTime.now())
                .messageType(celebrateMessage.getMessageType())
                .fundMoney(contributorRepository.findFundedAmount(wishItem, sender))
                .build();
        //메시지 레포에 저장?
        messageRepository.join(message);
        //이미지 저장
    }
}
