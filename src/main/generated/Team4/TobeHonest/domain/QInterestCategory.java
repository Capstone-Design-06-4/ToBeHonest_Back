package Team4.TobeHonest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInterestCategory is a Querydsl query type for InterestCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInterestCategory extends EntityPathBase<InterestCategory> {

    private static final long serialVersionUID = -1359138568L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInterestCategory interestCategory = new QInterestCategory("interestCategory");

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public QInterestCategory(String variable) {
        this(InterestCategory.class, forVariable(variable), INITS);
    }

    public QInterestCategory(Path<? extends InterestCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInterestCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInterestCategory(PathMetadata metadata, PathInits inits) {
        this(InterestCategory.class, metadata, inits);
    }

    public QInterestCategory(Class<? extends InterestCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category"), inits.get("category")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

