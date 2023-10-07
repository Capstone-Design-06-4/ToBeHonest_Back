package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.repo.MemberRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
public class LoginServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;
    @Test
    @DisplayName("암호화 확인")
    public void checkEncryption(){

        String email = "alswns2631@cau.ac.kr";
        String name = "choiminjun";
        String passWord = "passw123";
        String phoneNumber = "010-1234-1231";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        memberService.join(joinDTO);

        Member findMember = memberRepository.findByEmail(email);
        System.out.println(findMember.getPassword());
        Assertions.assertThat(findMember.getPassword()).isNotEqualTo(passWord);
    }
}
