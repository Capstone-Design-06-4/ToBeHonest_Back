package Team4.TobeHonest.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSendMessage is a Querydsl query type for SendMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSendMessage extends EntityPathBase<SendMessage> {

    private static final long serialVersionUID = 1821417135L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSendMessage sendMessage = new QSendMessage("sendMessage");

    public final QMessage _super;

    //inherited
    public final StringPath content;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final QWishItem relatedItem;

    public final QMember sender;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> time;

    public QSendMessage(String variable) {
        this(SendMessage.class, forVariable(variable), INITS);
    }

    public QSendMessage(Path<? extends SendMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSendMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSendMessage(PathMetadata metadata, PathInits inits) {
        this(SendMessage.class, metadata, inits);
    }

    public QSendMessage(Class<? extends SendMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QMessage(type, metadata, inits);
        this.content = _super.content;
        this.id = _super.id;
        this.relatedItem = _super.relatedItem;
        this.sender = inits.isInitialized("sender") ? new QMember(forProperty("sender")) : null;
        this.time = _super.time;
    }

}

