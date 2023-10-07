package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
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
    private final QFriendWith friendWith = new QFriendWith("friendWith");
    private final QContributor contributor = new QContributor("contributor");

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



    public List<WishItem> findAll(Member m){
        return jqf.select(wishItem).
                from(wishItem).
                where(wishItem.member.eq(m)).fetch();
    }


    private Long friendId;
    //    내가지정한 친구이름
    private String friendName;
    private Integer price;
    private String itemName;
    private String categoryName;
    private String image;

//    맨처음에 나오는 위시리스트 화면용
    public List<FirstWishItem> findFirstWishList(Member m){
//      집계함수
        NumberExpression<Integer> sumFundMoney = contributor.fundMoney.sum();
        //집계함수를 기준으로 내림차순. 이건 뭐 나중에 변경하면 되니까..
        return jqf.select(Projections.constructor(FirstWishItem.class, wishItem.id, item.image, sumFundMoney))
                .from(contributor)
                .innerJoin(contributor.wishItem, wishItem)
                .innerJoin(wishItem.item, item)
                .innerJoin(wishItem.member, member)
                .where(member.eq(m))
                .groupBy(wishItem.id)
                .orderBy(sumFundMoney.desc())
                .fetch();
    }



    public List<WishItemDetail> findWishItemDetail(Long wishItemId){
        NumberExpression<Integer> sumFundMoney = contributor.fundMoney.sum();
        return jqf.select(Projections.constructor(WishItemDetail.class, wishItem.id,
                item.name, item.price, sumFundMoney, item.image))
                .from(contributor)
                .innerJoin(contributor.wishItem, wishItem)
                .innerJoin(wishItem.item, item)
                .where(wishItem.id.eq(wishItemId))
                .groupBy(wishItem.id)
                .orderBy(sumFundMoney.desc())
                .fetch();
    }






}
