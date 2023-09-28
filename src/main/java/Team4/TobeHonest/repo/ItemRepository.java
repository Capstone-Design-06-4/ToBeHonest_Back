package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.Category;
import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.QCategory;
import Team4.TobeHonest.domain.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QItem i = new QItem("i");
    private final QCategory c = new QCategory("c");

    public void join(Item item) {
        em.persist(item);
    }

    public Item findByNaverId(Long id) {
        return em.find(Item.class, id);
    }

    //name을 포함하는 문자열 결과 물품 return




    public List<Item> findItem(String string) {

        return jqf.select(i).
                from(i).innerJoin(i.category, c).
                where(c.name.like("%"+ string + "%").
                        or(i.name.like("%"+ string + "%"))).fetch();

    }

    public List<Item> findAllItem(){
        return jqf.select(i).from(i).fetch();
    }


}
