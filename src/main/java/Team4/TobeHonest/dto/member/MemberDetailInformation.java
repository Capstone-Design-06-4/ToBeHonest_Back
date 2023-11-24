package Team4.TobeHonest.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDetailInformation {

    String name;
    String profileURL;

    LocalDate birthDate;
    Integer myPoints;

    Integer progressNum;
    Integer completedNum;
    Integer usedNoMsgNum;
    Integer usedMsgNum;

}
