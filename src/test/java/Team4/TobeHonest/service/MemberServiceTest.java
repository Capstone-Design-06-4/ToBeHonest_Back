package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.exception.NoPointsException;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Before
    public void setUp() throws Exception {
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

    }

    @DisplayName("포인트 충전 - 정상적으로 충전")
    @Test
    public void charge_points(){
        String email = "alswns2631@cau.ac.kr";
        Integer points = memberService.pointRecharge(email, 10000);
        Member member = memberService.findByEmail(email);
        Assertions.assertThat(member.getPoints()).isEqualTo(points);
    }

    @DisplayName("포인트 사용 - 정상 사용")
    @Test
    public void using_points(){
        String email = "alswns2631@cau.ac.kr";
        memberService.pointRecharge(email, 10000);
        memberService.usePoints(email, 5000);
        Member member = memberService.findByEmail(email);

        Assertions.assertThat(member.getPoints()).isEqualTo(5000);

    }

    @DisplayName("포인트 사용 - 과도 사용")
    @Test
    public void using_points_overFlow(){
        String email = "alswns2631@cau.ac.kr";
        memberService.pointRecharge(email, 10000);
        org.junit.jupiter.api.Assertions.assertThrows(NoPointsException.class,
                () -> memberService.usePoints(email, 15000));


    }

}
