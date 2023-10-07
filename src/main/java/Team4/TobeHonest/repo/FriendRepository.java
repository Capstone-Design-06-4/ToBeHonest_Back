package Team4.TobeHonest.repo;


import Team4.TobeHonest.domain.*;

import Team4.TobeHonest.dto.friendWIth.FriendWithSpecifyName;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QMember friend = new QMember("friend");
    private final QFriendWith friendWith = new QFriendWith("friendWith");
    private final QContributor contributor = new QContributor("contributor");

    public void join(FriendWith friend) {
        em.persist(friend);
    }

    public List<FriendWith> findFriend(Member owner, Member friend){
        return jqf.select(friendWith).
                from(friendWith).
                where(friendWith.owner.eq(owner).and(friendWith.friend.eq(friend))).
                fetch();
    }

    public List<FriendWith> findAllFriends(Member owner) {
        return jqf.select(friendWith).
                from(friendWith).
                where(friendWith.owner.eq(owner)).
                fetch();
    }

    //지정이름과 friend객체가 return..
    public List<FriendWithSpecifyName> findAllFriendsWithSpecifyName(Member owner) {

        return jqf.select(Projections.constructor(FriendWithSpecifyName.class, friendWith.id, friendWith.specifiedName, friend)).
                from(friendWith).innerJoin(friendWith.friend, friend).
                where(friendWith.owner.eq(owner)).fetch();

    }












}
