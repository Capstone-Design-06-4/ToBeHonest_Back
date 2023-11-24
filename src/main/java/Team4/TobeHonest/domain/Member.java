package Team4.TobeHonest.domain;

import Team4.TobeHonest.exception.NoPointsException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@NoArgsConstructor
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    //member_id와 중복이 있어서 memberId라 하지 않음.
    //private String userId;

    //  사실상 id 역할
    @Column(unique = true)
    private String email;
    private String name;
    private String password;

    private String phoneNumber;

    private LocalDate birthDate;

    private Integer points = 0;
    //프사 추가해야함
    private String profileImg = "https://tobehonest.s3.ap-northeast-2.amazonaws.com/default.jpeg";

    public void changeProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    @Builder
    public Member(String email, String name, String password, String phoneNumber, LocalDate birthDate) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    //    로그인 해시값 찾기
    public static String hashPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);

    }

    public void addPoints(Integer money){
        this.points += money;
    }

    public void usePoints(Integer money){
        if (points < money){
            throw new NoPointsException("포인트가 부족합니다.");
        }
        this.points -= money;
    }

    public FriendWith addFriend(Member friend) {
        return new FriendWith(this, friend);
    }

    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, this.password);
    }

    //getPassWord -> password(재귀호출...)
    @Override
    public String getPassword() {
        return this.password;
    }
    //getUserName -> this.email(재귀호출...)
    @Override
    public String getUsername() { return this.email; }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
