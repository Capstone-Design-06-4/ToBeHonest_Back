package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.enumer.GiftStatus;
import Team4.TobeHonest.enumer.IsThanksMessagedSend;
import Team4.TobeHonest.enumer.MessageType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WishItemRepository {
    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QWishItem wishItem = new QWishItem("wishItem");
    private final QMessage message = new QMessage("message");
    private final QItem item = new QItem("item");
    private final QMember member = new QMember("member");
    private final QFriendWith friendWith = new QFriendWith("friendWith");
    private final QContributor contributor = new QContributor("contributor");

    public void join(WishItem wishItem) {
        em.persist(wishItem);
    }

    public WishItem findWishItemById(Long id) {
        return em.find(WishItem.class, id);
    }

    public void deleteWishItem(WishItem wishItem) {
        wishItem.changeGiftStatus(GiftStatus.DELETED);
    }

    //    위시리스트에 있는 아이템 찾기..
    public WishItem findWishItem(Member member, Item item) {
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.eq(member).and(wishItem.giftStatus.ne(GiftStatus.DELETED))).fetch();
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
                .where(wishItem.member.eq(member)
                        .and(wishItem.giftStatus.ne(GiftStatus.DELETED))
                        .and(this.item.name.eq(itemName))).fetch();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public WishItem findWishItemByItemId(Member member, Long id) {
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.eq(member)
                        .and(this.item.id.eq(id))
                        .and(wishItem.giftStatus.ne(GiftStatus.DELETED)))
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public WishItem findWishItemByEmailAndItemName(String email, String itemName) {
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.email.eq(email)
                        .and(this.item.name.eq(itemName))
                        .and(wishItem.giftStatus.ne(GiftStatus.DELETED))
                )
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public WishItem findWishItemByIdAndItemName(Long friendId, String itemName) {
        List<WishItem> result = jqf.select(wishItem)
                .from(wishItem)
                .innerJoin(wishItem.item, this.item).innerJoin(wishItem.member, this.member)
                .where(wishItem.member.id.eq(friendId)
                        .and(this.item.name.eq(itemName))
                        .and(wishItem.giftStatus.ne(GiftStatus.DELETED)))
                .fetch();
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public List<WishItem> findAll(Member m) {
        return jqf.select(wishItem).
                from(wishItem).
                where(wishItem.member.eq(m)
                        .and(wishItem.giftStatus.ne(GiftStatus.DELETED))).fetch();
    }


    //    맨처음에 나오는 위시리스트 화면용
    public List<FirstWishItem> findFirstWishList(Member m) {
//      집계함수
        //집계함수를 기준으로 내림차순. 이건 뭐 나중에 변경하면 되니까..
        return jqf.select(Projections.constructor(FirstWishItem.class, wishItem.id, item.id, item.image, item.name, item.price, wishItem.isThanksMessagedSend))
                .from(wishItem)
                .innerJoin(wishItem.item, item)
                .innerJoin(wishItem.member, member)
                .where(member.eq(m).and(wishItem.giftStatus.ne(GiftStatus.DELETED))
                )
                .groupBy(wishItem.id)
                .fetch();

    }


    public List<WishItemDetail> findWishItemDetail(Long wishItemId) {
        return jqf.select(Projections.constructor(WishItemDetail.class, wishItem.id, item.id, item.name, item.price, item.image, wishItem.isThanksMessagedSend))
                .from(wishItem)
                .innerJoin(wishItem.item, item)
                .where(wishItem.id.eq(wishItemId))
                .groupBy(wishItem.id)
                .fetch();
    }

    //미 완료된 선물 찾기
    public List<FirstWishItem> findWishItemInProgress(Long memberId) {
        //집계함수를 기준으로 내림차순. 이건 뭐 나중에 변경하면 되니까..
        return jqf.select(Projections.constructor(FirstWishItem.class, wishItem.id, item.id, item.image, item.name, item.price, wishItem.isThanksMessagedSend))
                .from(wishItem)
                .innerJoin(wishItem.item, item)
                .innerJoin(wishItem.member, member)
                .where(member.id.eq(memberId)
                        .and(wishItem.giftStatus.eq(GiftStatus.IN_PROGRESS)))
                .groupBy(wishItem.id)
                .fetch();

    }

    //미 완료된 선물 찾기

    public List<FirstWishItem> findWishItemCompleted(Long memberId) {
        //집계함수를 기준으로 내림차순. 이건 뭐 나중에 변경하면 되니까..
        return jqf.select(Projections.constructor(FirstWishItem.class, wishItem.id, item.id, item.image, item.name, item.price, wishItem.isThanksMessagedSend))
                .from(wishItem)
                .innerJoin(wishItem.item, item)
                .innerJoin(wishItem.member, member)
                .where(member.id.eq(memberId)

                        .and(wishItem.giftStatus.eq(GiftStatus.COMPLETED)))
                .groupBy(wishItem.id)
                .fetch();

    }

    public List<FirstWishItem> findWishItemUsed(Long memberId) {
        //집계함수를 기준으로 내림차순. 이건 뭐 나중에 변경하면 되니까..
        return jqf.select(Projections.constructor(FirstWishItem.class, wishItem.id, item.id, item.image, item.name, item.price, wishItem.isThanksMessagedSend))
                .from(wishItem)
                .innerJoin(wishItem.item, item)
                .innerJoin(wishItem.member, member)
                .where(member.id.eq(memberId)
                        .and(wishItem.giftStatus.eq(GiftStatus.USED)))
                .groupBy(wishItem.id)
                .fetch();

    }




    public Integer countProgressNum(Member member){

        NumberExpression<Integer> progress = wishItem.count().intValue();
        Integer i = jqf.select(progress)
                .from(wishItem)
                .where(wishItem.member.eq(member)
                        .and(wishItem.giftStatus.eq(GiftStatus.IN_PROGRESS))).fetchOne();
        return (i == null) ? 0 : i;
    }

    public Integer completedNum(Member member){

        NumberExpression<Integer> progress = wishItem.count().intValue();
        Integer i = jqf.select(progress)
                .from(wishItem)
                .where(wishItem.member.eq(member)
                        .and(wishItem.giftStatus.eq(GiftStatus.COMPLETED))).fetchOne();

        return (i == null) ? 0 : i;
    }

    public Integer usedNoMsgNum(Member member){

        NumberExpression<Integer> progress = wishItem.count().intValue();
        Integer i = jqf.select(progress)
                .from(wishItem)
                .where(wishItem.member.eq(member)
                        .and(wishItem.giftStatus.eq(GiftStatus.USED))
                .and(wishItem.isThanksMessagedSend.eq(IsThanksMessagedSend.NOT_MESSAGED))).fetchOne();
        return (i == null) ? 0 : i;
//
    }
    public Integer usedMsgNum(Member member){

        NumberExpression<Integer> progress = wishItem.count().intValue();
        Integer i = jqf.select(progress)
                .from(wishItem)
                .where(wishItem.member.eq(member)
                        .and(wishItem.giftStatus.eq(GiftStatus.USED))
                        .and(wishItem.isThanksMessagedSend.eq(IsThanksMessagedSend.MESSAGED))
                ).fetchOne();

        return (i == null) ? 0 : i;
    }



    public Double checkPercentage(Item item1){

        int s = Math.max(item1.getPrice() - 50000, 0);

        int d = item1.getPrice() + 50000;
        NumberExpression<Integer> all = wishItem.count().intValue();
        NumberExpression<Integer> success = wishItem.count().intValue();
        NumberExpression<Integer> temp = wishItem.count().intValue();


        Integer allNum = jqf.select(all).from(wishItem)
                .where(wishItem.item.price.gt(s).and(wishItem.item.price.lt(d))).fetchOne();

        Integer i = jqf
                .select(temp)
                .from(wishItem)
                .where(wishItem.item.price.gt(s).and(wishItem.item.price.lt(d))
                        .and(wishItem.giftStatus.eq(GiftStatus.IN_PROGRESS))).fetchOne();
        if (i == null){
            i = 0;
        }



        if (allNum == null || allNum == 0)
            return 0.0;


        allNum -= Math.max(allNum - i, 0);
        Integer part = jqf.select(success).from(wishItem)
                .where(
                        (wishItem.price.gt(s).and(wishItem.price.lt(d)))
                                .and(wishItem.giftStatus.eq(GiftStatus.USED).or(wishItem.giftStatus.eq(GiftStatus.COMPLETED)))).fetchOne();
        if (part == null || part == 0)
            return 0.0;
        return (double) ((part* 100 / allNum) );

    }

}
