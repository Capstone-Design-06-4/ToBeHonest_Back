package Team4.TobeHonest.repo;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QMember m = new QMember("m");


    public void join(Member member) {
        em.persist(member);

    }

    public void deleteByEmail(String email) {
        Member memberByEmail = getMemberByEmail(email);
        if (memberByEmail != null) {
            em.remove(memberByEmail);
        }

    }

    //id 중복 찾기..
    //중복이면 false
    //중복아니면 true
    public Member getMemberByEmail(String email) {
        List<Member> find = jqf.select(m).from(m).where(m.email.eq(email)).fetch();
        if (find.isEmpty()) {
            return null;
        }
        return find.get(0);
    }

    public List<Member> findAll() {
        return jqf.select(m).from(m).fetch();
    }

    public Member loginFind(String email, String passWord) {
//        결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException
        return jqf.select(m).from(m).where(m.email.eq(email).and(m.passWord.eq(passWord))).fetchOne();

    }

//    친구리스트 찾기

}
