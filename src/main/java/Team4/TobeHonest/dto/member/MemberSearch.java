package Team4.TobeHonest.dto.member;

import Team4.TobeHonest.enumer.FriendStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSearch {
    Long memberId;
    String memberName;
    String profileImgURL;
    FriendStatus friendStatus;

}
