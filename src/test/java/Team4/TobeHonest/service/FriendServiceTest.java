package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.FriendWith;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.friendWIth.FriendWithSpecifyName;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.exception.DuplicateFriendException;
import Team4.TobeHonest.repo.FriendRepository;
import Team4.TobeHonest.repo.MemberRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
public class FriendServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    FriendService friendService;

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

        email = "alswns2631@gmail.com";
        name = "아이";
        phoneNumber = "010-1234-1234";
        joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        memberService.join(joinDTO);

        email = "alswns2631@daum.net";
        name = "아이";
        phoneNumber = "010-1234-0000";
        joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        memberService.join(joinDTO);

        email = "alswns2631@kakao.com";
        name = "아이";
        phoneNumber = "010-1234-0001";
        joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        memberService.join(joinDTO);

        email = "alswns2631@naver.com";
        name = "아이";
        phoneNumber = "010-1234-0002";
        joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        memberService.join(joinDTO);

    }

    @Test
    @DisplayName("친구 추가")
    public void testAddFreind() {
        //given
        Member member1 = memberService.findByEmail("alswns2631@cau.ac.kr");
        Member member2 = memberService.findByEmail("alswns2631@gmail.com");
        Member member3 = memberService.findByEmail("alswns2631@daum.net");
        Member member4 = memberService.findByEmail("alswns2631@kakao.com");
        Member member5 = memberService.findByPhoneNumber("010-1234-0002");
        friendService.addFriendList(member1, member2.getId());
        friendService.addFriendList(member1, member3.getId());
        friendService.addFriendList(member1, member4.getId());
        friendService.addFriendList(member1, member5.getId());
        //중복문제..
        //when
        List<FriendWithSpecifyName> allFriendsProfile = friendService.findAllFriendsProfile(member1);
        //then
        Assertions.assertThat(allFriendsProfile.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("친구 중복인 경우..")
    public void duplicateFriend() {
        Member member1 = memberService.findByEmail("alswns2631@cau.ac.kr");
        Member member2 = memberService.findByEmail("alswns2631@kakao.com");
        Member member3 = memberService.findByEmail("alswns2631@kakao.com");
        friendService.addFriendList(member1, member2.getId());
        org.junit.jupiter.api.Assertions.assertThrows(DuplicateFriendException.class,
                () -> friendService.addFriendList(member1, member3.getId()));

    }

    @Test
    @DisplayName("내가 지정한 이름")
    public void testSpecifyName(){
        Member member1 = memberService.findByEmail("alswns2631@cau.ac.kr");
        Member member2 = memberService.findByEmail("alswns2631@kakao.com");
        friendService.addFriendList(member1, member2.getId());
        FriendWith friendWith = friendRepository.findFriend(member1, member2).get(0);
        Assertions.assertThat(friendWith.getSpecifiedName()).isEqualTo(member2.getName());
        friendWith.changeFriendName("아이유");
        Assertions.assertThat(friendWith.getSpecifiedName()).isEqualTo("아이유");
    }


    @Test
    @DisplayName("친구삭제하기")
    public void deleteFriend(){
        Member member1 = memberService.findByEmail("alswns2631@cau.ac.kr");
        Member member2 = memberService.findByEmail("alswns2631@kakao.com");
        friendService.addFriendList(member1, member2.getId());
        FriendWith friendWith = friendRepository.findFriend(member1, member2).get(0);

        List<FriendWith> friend = friendRepository.findFriend(member1, member2.getId());
        Assertions.assertThat(friend).contains(friendWith);
        friendService.deleteFriend(member1, member2.getId());
        friend = friendRepository.findFriend(member1, member2.getId());
        Assertions.assertThat(friend).doesNotContain(friendWith);

    }

}

