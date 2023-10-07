package Team4.TobeHonest.dto;

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
