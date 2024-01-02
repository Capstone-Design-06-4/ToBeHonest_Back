package Team4.TobeHonest.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotNull(message = "이메일은 필수입니다.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;


    @NotNull(message = "비밀번호는 필수입니다.")
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하이어야 합니다.")
    private String password;


}
