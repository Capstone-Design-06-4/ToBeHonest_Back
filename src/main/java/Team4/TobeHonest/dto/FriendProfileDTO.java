package Team4.TobeHonest.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendProfileDTO {
    private Long friendWithId;
    private String specifyName;
    private LocalDate birthDate;
    //사진

    //builder용
    public FriendProfileDTO(String specifyName, LocalDate birthDate) {
        this.specifyName = specifyName;
        this.birthDate = birthDate;
    }

    //프사

    public static FriendProfileDTO makeFriendProfileDTO(String name, LocalDate birthDate) {
        FriendProfileDTO friendProfileDTO = new FriendProfileDTO();
        friendProfileDTO.specifyName = name;
        friendProfileDTO.birthDate = birthDate;
        //프로필 사진도 나중에 추가해야함..
        return friendProfileDTO;
    }
}
