package Team4.TobeHonest.dto.message;


import Team4.TobeHonest.enumer.MessageType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

public class MessageResponseDTO {
//server에서 제공하는 message.. ==>
    String itemName;
    String itemImage;

    String messageTitle;

    String messageContents;

    List<String> messageImgURLs = new ArrayList<>();

    Long senderId;

    Long receiverId;

    MessageType messageType;

    //얼마나 펀딩했는가..
    Integer fundMoney;
    @Builder

    public MessageResponseDTO(String itemName, String itemImage, String messageTitle, String messageContents, Long senderId, Long receiverId, MessageType messageType, Integer fundMoney) {
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.messageTitle = messageTitle;
        this.messageContents = messageContents;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.fundMoney = fundMoney;
    }
}
