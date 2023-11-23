package Team4.TobeHonest.dto.message.thanks;

import Team4.TobeHonest.enumer.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThanksWithNoImg {

    private Long wishItemId;

    private String title;
    private String contents;
    private MessageType messageType;
    //펀딩한 금액..
    //이미지와 json은 병렬화 할 수 없다..
}
