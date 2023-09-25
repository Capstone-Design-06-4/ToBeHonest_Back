package Team4.TobeHonest.dto;

import Team4.TobeHonest.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
//동명이인 Isuue 해결
public class FriendWithSpecifyName {
    private String specifiedName;
    private Member friend;

    public FriendWithSpecifyName(String specifiedName, Member friend) {
        this.specifiedName = specifiedName;
        this.friend = friend;
    }
}
