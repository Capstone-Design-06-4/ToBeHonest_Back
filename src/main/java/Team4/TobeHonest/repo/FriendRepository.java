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

    public void delete(FriendWith friend){em.remove(friend);}

    public List<FriendWith> findFriend(Member owner, Member friend) {
        return jqf.select(friendWith).
                from(friendWith).
                where(friendWith.owner.eq(owner).and(friendWith.friend.eq(friend))).
                fetch();
    }
    public List<FriendWith> findFriend(Member owner, Long friendId) {
        return jqf.select(friendWith).
                from(friendWith).
                where(friendWith.owner.eq(owner).and(friendWith.friend.id.eq(friendId))).
                fetch();
    }
    public List<FriendWith> findFriend(Long ownerId, Long friendId) {
        return jqf.select(friendWith).
                from(friendWith).
                where(friendWith.owner.id.eq(ownerId).and(friendWith.friend.id.eq(friendId))).
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

        return jqf.select(Projections.constructor(FriendWithSpecifyName.class,
                        friend.id, friendWith.id, friendWith.specifiedName, friend.birthDate,
                        friend.profileImg)).
                from(friendWith).innerJoin(friendWith.friend, friend).
                where(friendWith.owner.eq(owner)).fetch();

    }

    public List<FriendWithSpecifyName> searchFriendsWithName(Member member, String startsWith) {

        return jqf.
                select(Projections.constructor(FriendWithSpecifyName.class,
                        friend.id, friendWith.id, friendWith.specifiedName, friend.birthDate,
                        friend.profileImg))
                .from(friendWith)
                .innerJoin(friendWith.friend, friend)
                .where(friendWith.specifiedName.like("%" + startsWith + "%")
                        .and(friendWith.owner.eq(member))).fetch();
    }

    public List<FriendWithSpecifyName> searchFriendsWithEmail(Member member, String friendEmail) {

        return jqf.
                select(Projections.constructor(FriendWithSpecifyName.class,
                        friend.id, friendWith.id, friendWith.specifiedName, friend.birthDate,
                        friend.profileImg))
                .from(friendWith)
                .innerJoin(friendWith.friend, friend)
                .where(friendWith.friend.email.eq(friendEmail)
                        .and(friendWith.owner.eq(member))).fetch();
    }



    public List<Long> searchFriendsWithNameOnlyFriendId(Member member, String startsWith) {

        return jqf.
                select(friend.id)
                .from(friendWith)
                .innerJoin(friendWith.friend, friend)
                .where(friendWith.specifiedName.like("%" + startsWith + "%")
                        .and(friendWith.owner.eq(member))).fetch();
    }

}
