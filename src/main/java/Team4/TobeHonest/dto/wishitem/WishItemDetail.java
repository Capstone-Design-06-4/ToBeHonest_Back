package Team4.TobeHonest.dto.wishitem;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishItemDetail {
    Long wishItemId;
    String itemName;
    Integer total;
    Integer fund;
    String image;
}
