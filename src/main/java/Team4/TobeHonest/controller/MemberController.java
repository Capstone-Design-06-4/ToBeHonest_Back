package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.FriendWithSpecifyName;
import Team4.TobeHonest.service.FriendService;
import Team4.TobeHonest.service.MemberService;
import Team4.TobeHonest.utils.login.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MemberController {


    private final FriendService friendService;

    @GetMapping("{memberId}/Friends")
    //로그인은 인터셉터에서 처리 해 준다고 생각..
    public ResponseEntity findFriends(@PathVariable Long memberId, HttpServletRequest request){
        Member loginMember = (Member) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);

        //다른 회원정보에 들어가려고 하면 not allowed
        if (!memberId.equals(loginMember.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not allowed to other users information");
        }

        List<FriendWithSpecifyName> allFriendsProfile = friendService.findAllFriendsProfile(loginMember);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not allowed to other users information");

    }









}
