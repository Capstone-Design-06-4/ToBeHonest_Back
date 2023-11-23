package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class Item {
    @Id
    @GeneratedValue()
    @Column(name = "item_id")
    private Long id;
    private Long naverId;
    private String name;
    private Integer price;
    private String brandName;

    //이미지 파일 경로를 지정
    private String image;
    private Integer stockQuantity;
    @JoinColumn(name = "item_category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;


    public Item(Long naverId, String name, Integer price, String image, Category category) {
        this.naverId = naverId;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.stockQuantity = 100;
    }

    public void buyItem() {
        this.stockQuantity -= 1;
    }

    public void chargeItem(Integer stockQuantity){
        this.stockQuantity += stockQuantity;
    }


}
