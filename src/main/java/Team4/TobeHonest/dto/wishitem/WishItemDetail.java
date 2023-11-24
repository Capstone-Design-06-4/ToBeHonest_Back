package Team4.TobeHonest.dto.wishitem;

import Team4.TobeHonest.enumer.IsThanksMessagedSend;
import lombok.Builder;
import lombok.Data;


@Data
public class WishItemDetail {
    Long wishItemId;
    Long itemId;
    String itemName;
    Integer total;
    Integer fund;
    String image;
    IsThanksMessagedSend isThanksMessagedSend = IsThanksMessagedSend.NOT_MESSAGED;



    @Builder
    public WishItemDetail(Long wishItemId, Long itemId, String itemName, Integer total, String image, IsThanksMessagedSend isThanksMessagedSend) {
        this.wishItemId = wishItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.total = total;
        this.image = image;
        this.isThanksMessagedSend = isThanksMessagedSend;
    }
}
