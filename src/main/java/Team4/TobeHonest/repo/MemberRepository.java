package Team4.TobeHonest.repo;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.QMember;
import Team4.TobeHonest.domain.QWishItem;
import Team4.TobeHonest.dto.member.MemberDetailInformation;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
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
    private final QWishItem wishItem = new QWishItem("m");


    public void join(Member member) {
        em.persist(member);
    }

    public Member findById(Long memberId){
        return em.find(Member.class, memberId);
    }

    public void deleteByEmail(String email) {
        Member memberByEmail = findByEmail(email);
        if (memberByEmail != null) {
            em.remove(memberByEmail);
        }

    }

    public Member findByEmail(String email) {
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

        return jqf.select(m)
                .from(m)
                .where(m.email.eq(email).and(m.password.eq(passWord))).fetchOne();
    }

    public Member findByPhoneNumber(String phoneNumber) {
        List<Member> find = jqf
                .select(m)
                .from(m)
                .where(m.phoneNumber.eq(phoneNumber)).fetch();
        if (find.isEmpty()) {
            return null;
        }
        return find.get(0);
    }

    public MemberDetailInformation findMemberDetail(Member member){
       /* private final QWishItem wishItem1 = new QWishItem("m");
        private final QWishItem wishItem2 = new QWishItem("m");
        private final QWishItem wishItem3 = new QWishItem("m");
        NumberExpression<Integer> progress = wishItem.count().intValue();
        NumberExpression<Integer> completed = wishItem.count().intValue();
        NumberExpression<Integer> progress = wishItem.count().intValue();*/

        return jqf.select(Projections.constructor(
                MemberDetailInformation.class,
                m.name, m.profileImg, m.birthDate, m.points)).from(m).where(m.eq(member)).fetchOne();

    }


}
