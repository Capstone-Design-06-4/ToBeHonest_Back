package Team4.TobeHonest.dto.message.celebrate;

import Team4.TobeHonest.enumer.MessageType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCelebrateMessage {

    @NotNull
    private Long wishItemId;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    @Size(min = 1, max = 500)
    private String contents;

    private MessageType messageType;
    //펀딩한 금액..


}
