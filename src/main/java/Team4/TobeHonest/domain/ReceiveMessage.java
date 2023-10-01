package Team4.TobeHonest.domain;


import Team4.TobeHonest.enumer.ReadBit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ReceiveMessage extends Message {
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Enumerated(EnumType.STRING)
    private ReadBit readbit;


}
