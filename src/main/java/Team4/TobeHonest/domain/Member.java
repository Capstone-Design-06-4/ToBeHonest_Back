package Team4.TobeHonest.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    //member_id와 중복이 있어서 memberId라 하지 않음.
    //private String userId;

    //  사실상 id 역할
    private String email;
    private String name;
    private String passWord;

    private String phoneNumber;

    private LocalDate birthDate;
    //프사 추가해야함

    public Member(String email, String name, String passWord, String phoneNumber, LocalDate birthDate) {
        this.email = email;
        this.name = name;
        this.passWord = passWord;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public FriendWith addFriend(Member friend){

        return new FriendWith(this, friend);
    }


}
