package Team4.TobeHonest.dto.wishitem;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FriendWishItemInfoDTO {
    private Long friendId;
    private Integer price;
    private String itemName;
    private String categoryName;
    private String image;
    private Integer percentage;





}
