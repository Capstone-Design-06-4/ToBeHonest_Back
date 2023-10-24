package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import com.querydsl.core.types.Projections;
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
    private final QWishItem wishItem = new QWishItem("wi");
    private final QItem item = new QItem("i");

    public void join(Message message) {
        em.persist(message);
    }

    public List<Message> findAll() {
        return jqf.select(m).from(m).fetch();
    }

    //내가 보낸 message
    public List<Message> findMySent(Member member, Member friend) {
        return jqf.select(m)
                .from(m)
                .where(m.sender.eq(member)
                        .and(m.receiver.eq(friend))).fetch();
    }

    public List<Message> findMySent(Long memberId, Long friendId) {
        return jqf.select(m)
                .from(m)
                .where(m.sender.id.eq(memberId)
                        .and(m.receiver.id.eq(friendId))).fetch();
    }

    public List<Message> findMyReceive(Member member, Member friend) {
        return jqf.select(m).from(m).where(m.receiver.eq(member)
                .and(m.sender.eq(friend))).fetch();
    }


    public List<Message> findMyReceive(Long memberId, Long friendId) {
        return jqf.select(m).from(m).where(m.receiver.id.eq(memberId)
                .and(m.sender.id.eq(friendId))).fetch();
    }

    public List<MessageResponseDTO> msgWithMyFriend(Long memberId, Long friendId) {
        return jqf.select(
                        Projections.constructor(
                                MessageResponseDTO.class,
                                item.name, item.image, m.title, m.content, m.sender.id, m.receiver.id))
                .from(m).innerJoin(m.relatedItem, this.wishItem)
                .innerJoin(this.wishItem.item, this.item)
                .where(m.receiver.id.eq(memberId)
                        .and(m.sender.id.eq(friendId))
                        .or((m.sender.id.eq(memberId)
                                .and(m.receiver.id.eq(friendId))))).orderBy(m.time.asc()).fetch();

    }


}
