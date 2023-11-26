package Team4.TobeHonest.dto.wishitem;


import Team4.TobeHonest.enumer.IsThanksMessagedSend;
import lombok.*;

//최초 위시리스트에 나오는 정보들..
//사진이랑 퍼센티지
@Data
public class FirstWishItem {
    private Long wishItemId;

    private Long itemId;

    private String image;

    private String itemName;
    //총가격
    private Integer itemPrice;
    //펀딩된 총 가격
    private Integer fundAmount;

    private IsThanksMessagedSend isMessaged = IsThanksMessagedSend.NOT_MESSAGED;



    @Builder
    public FirstWishItem(Long wishItemId, Long itemId, String image, String itemName, Integer itemPrice) {
        this.wishItemId = wishItemId;
        this.itemId = itemId;
        this.image = image;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    @Builder
    public FirstWishItem(Long wishItemId, Long itemId, String image, String itemName, Integer itemPrice, IsThanksMessagedSend isMessaged) {
        this.wishItemId = wishItemId;
        this.itemId = itemId;
        this.image = image;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.isMessaged = isMessaged;
    }
}
