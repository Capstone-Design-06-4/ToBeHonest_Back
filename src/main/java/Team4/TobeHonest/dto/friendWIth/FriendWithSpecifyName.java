package Team4.TobeHonest.dto.friendWIth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendWithSpecifyName {
    private Long friendId;
    private Long friendWithId;
    private String specifiedName;
    private LocalDate birthDate;
    //사진
    private String profileURL;
    //내가 보낸적이 있는가
    private Boolean myGive;
    //내가 받은적이 있는가
    private Boolean myTake;

    public FriendWithSpecifyName(Long friendId, Long friendWithId, String specifiedName, LocalDate birthDate, String profileURL) {
        this.friendId = friendId;
        this.friendWithId = friendWithId;
        this.specifiedName = specifiedName;
        this.birthDate = birthDate;
        this.profileURL = profileURL;
    }

    public void setMyGive(Boolean myGive) {
        this.myGive = myGive;
    }

    public void setMyTake(Boolean myTake) {
        this.myTake = myTake;
    }
}
