package Team4.TobeHonest.setup;

import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendJoinService {

    @Autowired
    public MemberService memberService;

    public void saveMemberInDB() {
        String email = "alswns2631@cau.ac.kr";
        String name = "choiminjun";
        String passWord = "passw123";
        String phoneNumber = "010-1234-1231";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        memberService.join(joinDTO);
        email = "alswns2631@gmail.com";
        name = "정상수";
        joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        memberService.join(joinDTO);
        email = "alswns2631@naver.com";
        name = "아이유";
        joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        memberService.join(joinDTO);
    }
}
