package Team4.TobeHonest.dto.contributor;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

//wishItem에 contribute 해준 친구들..
public class ContributorDTO {
    public Long wishItemId;
    public Long friendId;
    public String friendName;
    private Integer contribution;
}
