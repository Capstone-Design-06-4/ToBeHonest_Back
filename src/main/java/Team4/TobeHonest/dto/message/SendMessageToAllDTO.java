package Team4.TobeHonest.dto.message;

import Team4.TobeHonest.enumer.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageToAllDTO {
    private Long wishItemId;
    private Long senderId;
    private String title;
    private String contents;
    private MessageType messageType;
    private Integer fundMoney;


}
