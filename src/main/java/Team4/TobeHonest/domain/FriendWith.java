package Team4.TobeHonest.domain;

import Team4.TobeHonest.enumer.RelationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

//친구들의 manyTomany관계를 표현하기 위해서..
@Entity
@Getter
@NoArgsConstructor
public class FriendWith {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member owner;

    @JoinColumn(name = "friend_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member friend;

    @Enumerated(EnumType.STRING)
    private RelationType relationType;

    private String specifiedName;

    public FriendWith(Member owner, Member friend) {
        this.owner = owner;
        this.friend = friend;
        relationType = RelationType.FRIEND;
        specifiedName = friend.getName();
    }

    public void changeFriendType(RelationType relationType) {
        this.relationType = relationType;
    }

    public String changeFriendName(String name) {
        this.specifiedName = name;
        return this.specifiedName;
    }

}
