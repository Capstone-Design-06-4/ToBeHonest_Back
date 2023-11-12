package Team4.TobeHonest.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class MessageImg {
    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;


    private String imgURL;

    @Builder
    public MessageImg(Message message, String imgURL) {
        this.message = message;
        this.imgURL = imgURL;
    }

}
