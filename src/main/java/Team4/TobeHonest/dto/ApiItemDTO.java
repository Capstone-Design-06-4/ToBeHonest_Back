package Team4.TobeHonest.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
//API로 받아올 떄 실행하는 정보
public class ApiItemDTO {
    private Long id;
    private String itemName;

    private String image;
    private int lprice;
    private List<String> categoryList = new ArrayList<>();


    public ApiItemDTO(JSONObject itemJson) {
        this.id = itemJson.getLong("productId");
        this.itemName = itemJson.getString("title");
        this.image = itemJson.getString("image");
        this.lprice = itemJson.getInt("lprice");
        String c1 = itemJson.getString("category1");
        String c2 = itemJson.getString("category2");
        String c3 = itemJson.getString("category3");
        String c4 = itemJson.getString("category4");
        categoryList.add(c1);
        categoryList.add(c2);
        categoryList.add(c3);
        categoryList.add(c4);

    }



}
