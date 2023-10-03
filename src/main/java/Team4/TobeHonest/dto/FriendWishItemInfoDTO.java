package Team4.TobeHonest.dto;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FriendWishItemInfoDTO {
    private String friendEmail;
    private String friendName;
    private Integer price;
    private String itemName;
    private String categoryName;
    private String image;



}
