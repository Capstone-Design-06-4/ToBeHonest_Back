package Team4.TobeHonest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReceiveMessage is a Querydsl query type for ReceiveMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReceiveMessage extends EntityPathBase<ReceiveMessage> {

    private static final long serialVersionUID = 1066520148L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReceiveMessage receiveMessage = new QReceiveMessage("receiveMessage");

    public final QMessage _super;

    //inherited
    public final StringPath content;

    //inherited
    public final NumberPath<Long> id;

    public final EnumPath<Team4.TobeHonest.enumer.ReadBit> readbit = createEnum("readbit", Team4.TobeHonest.enumer.ReadBit.class);

    public final QMember receiver;

    // inherited
    public final QWishItem relatedItem;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> time;

    public QReceiveMessage(String variable) {
        this(ReceiveMessage.class, forVariable(variable), INITS);
    }

    public QReceiveMessage(Path<? extends ReceiveMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReceiveMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReceiveMessage(PathMetadata metadata, PathInits inits) {
        this(ReceiveMessage.class, metadata, inits);
    }

    public QReceiveMessage(Class<? extends ReceiveMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QMessage(type, metadata, inits);
        this.content = _super.content;
        this.id = _super.id;
        this.receiver = inits.isInitialized("receiver") ? new QMember(forProperty("receiver")) : null;
        this.relatedItem = _super.relatedItem;
        this.time = _super.time;
    }

}

