package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.message.MessageResponseDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QMessage m = new QMessage("m");
    private final QMessageImg qMessageImg = new QMessageImg("img");
    private final QWishItem wishItem = new QWishItem("wi");
    private final QItem item = new QItem("i");

    private final QMessageImg qMessageImg = new QMessageImg("img");

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
                                wishItem.id, item.id, m.id,
                                item.name, item.image, m.title, m.content, m.sender.id, m.receiver.id, m.messageType, m.fundMoney))
                .from(m).
                innerJoin(m.relatedItem, this.wishItem)
                .innerJoin(this.wishItem.item, this.item)
                .where(m.receiver.id.eq(memberId)
                        .and(m.sender.id.eq(friendId))
                        .or((m.sender.id.eq(memberId)
                                .and(m.receiver.id.eq(friendId))))).orderBy(m.time.asc()).fetch();

    }


    public List<MessageResponseDTO> msgWithWithWishItem(Long wishItemId) {
        return jqf.select(
                        Projections.constructor(
                                MessageResponseDTO.class,
                                this.wishItem.id, item.id, m.id,
                                item.name, item.image, m.title, m.content, m.sender.id, m.receiver.id, m.messageType, m.fundMoney))
                .from(m).innerJoin(m.relatedItem, this.wishItem)
                .innerJoin(this.wishItem.item, this.item)
                .where(wishItem.id.eq(wishItemId))
                .orderBy(m.time.asc()).fetch();

    }


    public void saveAll(List<Message> messages) {
        messages.forEach(em::persist);
    }

    public List<String> findMessageImg(Long msgId) {
        return jqf.select(qMessageImg.imgURL).from(qMessageImg).where(qMessageImg.message.id.eq(msgId)).fetch();
    }
}
