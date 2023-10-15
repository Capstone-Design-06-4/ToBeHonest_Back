package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.Message;
import Team4.TobeHonest.domain.QMessage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QMessage m = new QMessage("m");

    public void join(Message message) {
        em.persist(message);
    }
    public List<Message> findAll() {
        return jqf.select(m).from(m).fetch();
    }
}
