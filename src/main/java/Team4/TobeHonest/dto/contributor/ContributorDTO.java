package Team4.TobeHonest.dto.contributor;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class ContributorDTO {
    public Long wishItemId;
    //wishItem에 contribute 해준 친구 Id를 저장
    public Long friendId;
    public String friendName;
    public String profileImg;
    private Integer contribution;
}
