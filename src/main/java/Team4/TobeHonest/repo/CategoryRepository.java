package Team4.TobeHonest.repo;

import Team4.TobeHonest.domain.Category;
import Team4.TobeHonest.domain.FriendWith;
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
//CRUD
public class CategoryRepository {

    private final EntityManager em;
    private final JPAQueryFactory jqf;
    private final QCategory category = new QCategory("category");


    public void join(Category category) {
        em.persist(category);
    }
    public Category findByName(String name){
        List<Category> fetch = jqf.select(category).from(category).where(category.name.eq(name)).fetch();
        if (fetch.isEmpty()){
            return null;
        }
        return fetch.get(0);

    }
}
