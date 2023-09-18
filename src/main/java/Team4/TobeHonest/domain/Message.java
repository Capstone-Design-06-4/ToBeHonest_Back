package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class  Message{
    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;

//    이건 타입 미지정
    private String content;

    private LocalDateTime time;

    @JoinColumn(name = "related_item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WishItem relatedItem;

}
