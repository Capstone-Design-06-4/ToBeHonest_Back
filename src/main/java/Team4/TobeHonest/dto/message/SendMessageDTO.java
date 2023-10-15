package Team4.TobeHonest.dto.message;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO {

    private Long wishItemId;
    private Long senderId;
    private Long receiverId;
    private String title;
    private String contents;
    private List<MultipartFile> images = new ArrayList<>();




}
