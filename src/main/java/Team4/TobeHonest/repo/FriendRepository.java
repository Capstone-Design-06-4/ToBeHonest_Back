package Team4.TobeHonest.repo;


import Team4.TobeHonest.domain.FriendWith;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.QFriendWith;
import Team4.TobeHonest.domain.QMember;
import Team4.TobeHonest.dto.FriendProfileDTO;
import Team4.TobeHonest.dto.FriendWithSpecifyName;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FriendRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QMember friend = new QMember("friend");
    private final QFriendWith f = new QFriendWith("f");

    public void join(FriendWith friend) {
        em.persist(friend);
    }

    public List<FriendWith> findFriend(Member owner, Member friend){
        return jqf.select(f).
                from(f).
                where(f.owner.eq(owner).and(f.friend.eq(friend))).
                fetch();
    }

    public List<FriendWith> findAllFriends(Member owner) {
        return jqf.select(f).
                from(f).
                where(f.owner.eq(owner)).
                fetch();
    }

    //지정이름과 friend객체가 return..
    public List<FriendWithSpecifyName> findAllFriendsWithSpecifyName(Member owner) {

        return jqf.select(Projections.constructor(FriendWithSpecifyName.class, f.specifiedName, friend)).
                from(f).innerJoin(f.friend, friend).
                where(f.owner.eq(owner)).fetch();

    }












}
