package Team4.TobeHonest.dto;

import Team4.TobeHonest.domain.Member;
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
    private Long friendWithId;
    private String specifiedName;
    private Member friend;



}
