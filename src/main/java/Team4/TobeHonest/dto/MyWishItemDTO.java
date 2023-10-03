package Team4.TobeHonest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MyWishItemDTO {
    private Integer price;
    private String itemName;
    private String categoryName;
    private String image;
}
