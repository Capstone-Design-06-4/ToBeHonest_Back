package Team4.TobeHonest.domain;


import Team4.TobeHonest.enumer.MessageType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    private String title;

    //    글내용
    private String content;

    private LocalDateTime time;

    private Integer fundMoney;

    @JoinColumn(name = "related_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WishItem relatedItem;

    //  메세지가 삭제되면 같이 보낸 사진들은 의미가 없어진다.. 그래서 한꺼번에 관리용으로 cascade 설정
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    private List<MessageImg> messageImgList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    private MessageType messageType;

    @Builder
    public Message(String title, String content, LocalDateTime time, WishItem relatedItem, Member sender, Member receiver, MessageType messageType, Integer fundMoney) {

        this.title = title;
        this.content = content;
        this.time = time;
        this.relatedItem = relatedItem;
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.fundMoney = fundMoney;
    }



//    연관관계 설정 메서드

    public MessageImg addImage(String imgURL) {
        MessageImg messageImg = MessageImg.builder().message(this).imgURL(imgURL).build();
        this.messageImgList.add(messageImg);
        return messageImg;
    }

}
