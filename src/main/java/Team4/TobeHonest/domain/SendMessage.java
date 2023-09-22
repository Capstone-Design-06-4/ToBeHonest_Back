package Team4.TobeHonest.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class SendMessage extends Message {

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;
}
