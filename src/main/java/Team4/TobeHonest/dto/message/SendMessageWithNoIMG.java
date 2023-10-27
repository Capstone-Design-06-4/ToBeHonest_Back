package Team4.TobeHonest.dto.message;

import Team4.TobeHonest.enumer.MessageType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageWithNoIMG {

    private Long wishItemId;
    private Long senderId;
    private Long receiverId;
    private String title;
    private String contents;
    private MessageType messageType;
    private Integer fundMoney;
}
