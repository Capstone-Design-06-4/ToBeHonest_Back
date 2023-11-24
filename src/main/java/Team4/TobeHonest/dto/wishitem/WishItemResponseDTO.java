package Team4.TobeHonest.dto.wishitem;


import Team4.TobeHonest.dto.contributor.ContributorDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishItemResponseDTO {
    private List<WishItemDetail> wishItemDetail = new ArrayList<>();
    private List<ContributorDTO> contributor = new ArrayList<>();
}
