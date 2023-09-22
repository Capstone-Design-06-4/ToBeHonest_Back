package Team4.TobeHonest.dto;

import Team4.TobeHonest.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

//회원가입 용
//왜 만드냐.. ==> setter 줄이기,
//
@Getter
@Setter
@Builder
@NoArgsConstructor
public class JoinDTO {
    @NotBlank(message = "이메일을 입력해 주세요")
    @Email(message = "이메일 형식에 맞지 않습니다")
    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 , 10자 이하여야 합니다.")
    private String name;
    //    (숫자, 문자 포함의 6~12자리 이내)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
    @Size(min = 6, max = 12, message = "비밀번호는 6자 이상 , 12자 이하여야 합니다.")
    private String passWord;

    //xxx-xxxx-xxx꼴
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식이 맞지 않습니다")
    private String phoneNumber;

    //    YYYYMMDD형태
    @Pattern(regexp = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])", message = "생일 형식에 맞지 않습니다.")
    private String birthDate;

    public JoinDTO(String email, String name, String passWord, String phoneNumber, String birthDate) {
        this.email = email;
        this.name = name;
        this.passWord = passWord;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public Member toMember() {
        int year = Integer.parseInt(birthDate.substring(0, 4));
        int month = Integer.parseInt(birthDate.substring(4, 6));
        int day = Integer.parseInt(birthDate.substring(6, 8));
        return new Member(this.email, this.name, this.passWord, this.phoneNumber, LocalDate.of(year, month, day));

    }

    //joinDTO ==> member로 바꾸기
}
