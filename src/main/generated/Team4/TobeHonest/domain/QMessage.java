package Team4.TobeHonest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMessage is a Querydsl query type for Message
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMessage extends EntityPathBase<Message> {

    private static final long serialVersionUID = 1815676759L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMessage message = new QMessage("message");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> fundMoney = createNumber("fundMoney", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<MessageImg, QMessageImg> messageImgList = this.<MessageImg, QMessageImg>createList("messageImgList", MessageImg.class, QMessageImg.class, PathInits.DIRECT2);

    public final EnumPath<Team4.TobeHonest.enumer.MessageType> messageType = createEnum("messageType", Team4.TobeHonest.enumer.MessageType.class);

    public final QMember receiver;

    public final QWishItem relatedItem;

    public final QMember sender;

    public final DateTimePath<java.time.LocalDateTime> time = createDateTime("time", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QMessage(String variable) {
        this(Message.class, forVariable(variable), INITS);
    }

    public QMessage(Path<? extends Message> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMessage(PathMetadata metadata, PathInits inits) {
        this(Message.class, metadata, inits);
    }

    public QMessage(Class<? extends Message> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new QMember(forProperty("receiver")) : null;
        this.relatedItem = inits.isInitialized("relatedItem") ? new QWishItem(forProperty("relatedItem"), inits.get("relatedItem")) : null;
        this.sender = inits.isInitialized("sender") ? new QMember(forProperty("sender")) : null;
    }

}

