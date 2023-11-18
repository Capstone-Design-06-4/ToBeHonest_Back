package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Contributor {
    @Id
    @GeneratedValue()
    private Long id;

    @JoinColumn(name = "wish_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WishItem wishItem;

    @JoinColumn(name = "contributor_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member contributor;

    private Integer fundMoney;

    private LocalDateTime fundDateTime;

    @Builder
    public Contributor(WishItem wishItem, Member contributor, Integer fundMoney, LocalDateTime fundDateTime) {
        this.wishItem = wishItem;
        this.contributor = contributor;
        this.fundMoney = fundMoney;
        this.fundDateTime = fundDateTime;
    }

    //    나중에 합치기용..
    public void changeWishItem(WishItem wishItem) {
        this.wishItem = wishItem;
    }

    public void addFundMoney(Integer money){
        this.fundMoney += money;
    }
}
