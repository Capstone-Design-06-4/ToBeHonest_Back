package Team4.TobeHonest.domain;


import jakarta.persistence.*;
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
    private String name;
    private Integer price;

    @JoinColumn(name = "item_category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

}
