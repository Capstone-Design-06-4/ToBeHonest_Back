package Team4.TobeHonest.dto.message.celebrate;

import Team4.TobeHonest.enumer.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCelebrateMessage {
    private Long wishItemId;
    private String title;
    private String contents;
    private MessageType messageType;
    //펀딩한 금액..


}
