package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor

public class WishItemRepository {
    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QWishItem wishItem = new QWishItem("wishItem");
    private final QItem item = new QItem("item");
    private final QMember member = new QMember("member");

    public void join(WishItem wishItem) {
        em.persist(wishItem);
    }

    public void deleteWishItem(WishItem wishItem) {
        em.remove(wishItem);
    }

    //    위시리스트에 있는 아이템 찾기..
    public WishItem findWishItem(Member member, Item item) {
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.eq(member))
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    //    member랑 item이름으로 wishItem찾기
    public WishItem findWishItemByItemName(Member member, String itemName) {
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.eq(member).and(this.item.name.eq(itemName)))
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public WishItem findWishItemByEmailAndItemName(String email, String itemName){
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.email.eq(email).and(this.item.name.eq(itemName)))
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public WishItem findWishItemByIdAndItemName(Long friendId, String itemName){
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.id.eq(friendId).and(this.item.name.eq(itemName)))
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }



    public List<WishItem> findAll(Member member){
        return jqf.select(wishItem).
                from(wishItem).
                where(wishItem.member.eq(member)).fetch();
    }
}
