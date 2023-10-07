package Team4.TobeHonest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MyWishItemDTO {
    private Integer price;
    private String itemName;
    private String categoryName;
    private String image;
    //투자한 살마들..
    private Map<String, Integer> contributor = new ConcurrentHashMap<>();
}
