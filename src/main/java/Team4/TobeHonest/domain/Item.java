package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    private Long naverId;
    private String name;
    private Integer price;

    //이미지 파일 경로를 지정
    private String image;
    private Integer stockQuantity;
    @JoinColumn(name = "item_category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;


    public Item(Long naverid, String name, Integer price, String image, Category category) {
        this.naverId = naverid;
        this.name = name;
        this.price = price;
        this.image = image;
        this.category = category;
        this.stockQuantity = 100;
    }

}
