package Team4.TobeHonest.dto.message.celebrate;

import Team4.TobeHonest.enumer.MessageType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SendCelebrateMessage {
    private Long wishItemId;
    private String title;
    private String contents;
    private MessageType messageType;
    //펀딩한 금액..

}
