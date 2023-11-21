package Team4.TobeHonest.dto.message.thanks;

import Team4.TobeHonest.enumer.MessageType;
import lombok.Data;

@Data
public class ThanksWithNoImg {

    private Long wishItemId;

    private String title;
    private String contents;
    private MessageType messageType;
    //펀딩한 금액..
    private Integer fundMoney;
    //이미지와 json은 병렬화 할 수 없다..
}
