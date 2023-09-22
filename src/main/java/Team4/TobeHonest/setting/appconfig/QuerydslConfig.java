package Team4.TobeHonest.setting.appconfig;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory query() {
        return new JPAQueryFactory(em);
    }
}