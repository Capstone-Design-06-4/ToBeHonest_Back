package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.contributor.ContributorDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

//WishItem에서 다른 WishItem으로 변경해야하기 때문에 별도의 repository를 만들었음
@Repository
@RequiredArgsConstructor

public class ContributorRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;

    private final QWishItem wishItem = new QWishItem("wishItem");
    private final QFriendWith friendWith = new QFriendWith("friendWith");
    private final QContributor contributor = new QContributor("contributor");
    private final QMember member = new QMember("member");
    private final QItem item = new QItem("item");

    public void join(Contributor contributor) {
        em.persist(contributor);
    }

    //    contributor가 wishItem에 참여했는가
//    내가 지정한 friendName
    public Contributor findContributionWithNamesByEmail(Member member, String email, String itemName) {
        List<Contributor> fetch = jqf.select(this.contributor)
                .from(this.contributor)
                .innerJoin(this.contributor.wishItem, this.wishItem)
                .innerJoin(this.wishItem.item, this.item)
                .innerJoin(this.wishItem.member, this.member)
                .where(this.contributor.contributor.eq(member)
                        .and(this.item.name.eq(itemName))
                        .and(this.member.email.eq(email))).fetch();

        if (fetch.isEmpty()) {
            return null;
        }
        return fetch.get(0);
    }

    public Contributor findContributionWithNamesById(Member member, Long id, String itemName) {
        List<Contributor> fetch = jqf.select(this.contributor)
                .from(this.contributor)
                .innerJoin(this.contributor.wishItem, this.wishItem)
                .innerJoin(this.wishItem.item, this.item)
                .innerJoin(this.wishItem.member, this.member)
                .where(this.contributor.contributor.eq(member)
                        .and(this.item.name.eq(itemName))
                        .and(this.member.id.eq(id))).fetch();

        if (fetch.isEmpty()) {
            return null;
        }
        return fetch.get(0);
    }

    public List<ContributorDTO> findContributorsInWishItem(Long wid) {
        return jqf.select(Projections.constructor(ContributorDTO.class, wishItem.id,
                        contributor.contributor.id, contributor.fundMoney))
                .from(contributor, friendWith)
                .innerJoin(contributor.wishItem, wishItem)
                .where(this.wishItem.id.eq(wid)
                        .and(wishItem.member.eq(friendWith.owner))
                        .and(friendWith.friend.eq(contributor.contributor))).fetch();

    }


    public Contributor findContributorsInWishItem(Long wid, Member member) {
        List<Contributor> fetch = jqf.select(contributor)
                .from(contributor)
                .innerJoin(contributor.wishItem, wishItem)
                .where(contributor.contributor.eq(member)
                        .and(wishItem.id.eq(wid))).fetch();

        if (fetch.isEmpty()){
            return null;
        }
        return fetch.get(0);
    }

    //나에게 contribution한 사람들 찾기
    public List<Long> findAllContributors(Long memberId){
        return jqf.select(contributor.contributor.id)
                .from(contributor)
                .innerJoin(contributor.wishItem, wishItem)
                .where(wishItem.member.id.eq(memberId)).fetch();

    }
    //내가 contribution한 놈들 찾기..
    public List<Long> findMyContributions(Long memberId){
        return jqf.select(wishItem.member.id)
                .from(contributor)
                .innerJoin(contributor.wishItem, wishItem)
                .innerJoin(wishItem.member, this.member)
                .where(contributor.contributor.id.eq(memberId)
                        .and(contributor.wishItem.eq(wishItem))).fetch();

    }

    public Integer findFundedAmount(WishItem wishItem){
        NumberExpression<Integer> sumFundMoney = contributor.fundMoney.sum();
        return jqf.select(sumFundMoney).from(contributor)
                .innerJoin(contributor.wishItem, this.wishItem)
                .where(this.wishItem.eq(wishItem)).fetchOne();
    }

}
