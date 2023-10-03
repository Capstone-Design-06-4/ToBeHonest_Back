package Team4.TobeHonest.dto;


import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.enumer.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


//이 DTO클래스는 아이템 검색용
//그래서 검색한 결과를 HTTP API형태로 보여주려고 한다
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ItemInfoDTO {

    private Integer price;
    private String name;
    private String categoryName;
    private String image;
    private ItemStatus itemStatus;

    public static ItemInfoDTO ItemToItemInfoDTO(Item item) {
        ItemStatus is = ItemStatus.AVAILABLE;
        if (item.getStockQuantity() <= 0) {
            is = ItemStatus.SOLD_OUT;
        }

        return ItemInfoDTO.builder()
                .price(item.getPrice())
                .name(item.getName())
                .categoryName(item.getCategory().getName())
                .image(item.getImage())
                .itemStatus(is)
                .build();
    }

}
