package Team4.TobeHonest.dto.wishitem;


import lombok.*;

//최초 위시리스트에 나오는 정보들..
//사진이랑 퍼센티지
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirstWishItem {
    private Long wishItemId;
    private String image;
    //총가격
    private Integer itemPrice;
    //펀딩된 총 가격
    private Integer fundAmount;


}
