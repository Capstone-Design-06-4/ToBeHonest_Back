package Team4.TobeHonest.dto.wishitem;

import lombok.Builder;
import lombok.Data;


@Data

public class WishItemDetail {
    Long wishItemId;
    String itemName;
    Integer total;
    Integer fund;
    String image;

    @Builder
    public WishItemDetail(Long wishItemId, String itemName, Integer total, String image) {
        this.wishItemId = wishItemId;
        this.itemName = itemName;
        this.total = total;
        this.image = image;
    }


}
