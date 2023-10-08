package Team4.TobeHonest.dto.friendWIth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//동명이인 Isuue 해결
public class FriendWithSpecifyName {
    private Long friendId;
    private Long friendWithId;
    private String specifiedName;




}
