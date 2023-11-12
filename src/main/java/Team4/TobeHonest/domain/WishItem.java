package Team4.TobeHonest.domain;

import Team4.TobeHonest.enumer.GiftStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class WishItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_item_id")
    private Long id;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Integer price;

    @Enumerated(EnumType.STRING)
    //오류뜨는데, 나중에 바뀐다..
    private GiftStatus giftStatus = GiftStatus.IN_PROGRESS;

    @Builder
    public WishItem(Item item, Member member, Integer money) {
        this.item = item;
        this.member = member;
        this.price = money;
    }

    public void changeGiftStatus(GiftStatus giftStatus) {
        this.giftStatus = giftStatus;
    }
}
