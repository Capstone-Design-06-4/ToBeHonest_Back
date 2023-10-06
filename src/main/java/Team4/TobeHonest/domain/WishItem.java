package Team4.TobeHonest.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class WishItem {
    @Id
    @GeneratedValue
    @Column(name = "wish_item_id")
    private Long id;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Integer price;

    @Builder
    public WishItem(Item item, Member member, Integer money) {
        this.item = item;
        this.member = member;
        this.price = money;
    }
}
