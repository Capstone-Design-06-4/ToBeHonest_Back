package Team4.TobeHonest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendWith is a Querydsl query type for FriendWith
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendWith extends EntityPathBase<FriendWith> {

    private static final long serialVersionUID = -57563340L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendWith friendWith = new QFriendWith("friendWith");

    public final QMember friend;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember owner;

    public final EnumPath<Team4.TobeHonest.enumer.RelationType> relationType = createEnum("relationType", Team4.TobeHonest.enumer.RelationType.class);

    public final StringPath specifiedName = createString("specifiedName");

    public QFriendWith(String variable) {
        this(FriendWith.class, forVariable(variable), INITS);
    }

    public QFriendWith(Path<? extends FriendWith> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendWith(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendWith(PathMetadata metadata, PathInits inits) {
        this(FriendWith.class, metadata, inits);
    }

    public QFriendWith(Class<? extends FriendWith> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.friend = inits.isInitialized("friend") ? new QMember(forProperty("friend")) : null;
        this.owner = inits.isInitialized("owner") ? new QMember(forProperty("owner")) : null;
    }

}

