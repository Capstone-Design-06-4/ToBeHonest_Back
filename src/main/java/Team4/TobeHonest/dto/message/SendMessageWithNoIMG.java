package Team4.TobeHonest.dto.message;

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
}
