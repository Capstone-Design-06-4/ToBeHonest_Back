package Team4.TobeHonest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class LoginDTO {
    @NotEmpty
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;
    @NotEmpty
    private String passWord;

    public LoginDTO(String email, String passWord) {
        this.email = email;
        this.passWord = passWord;
    }

}
