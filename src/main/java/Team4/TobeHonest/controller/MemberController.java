package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.friendWIth.FriendWithSpecifyName;
import Team4.TobeHonest.dto.member.MemberSearch;
import Team4.TobeHonest.exception.DuplicateFriendException;
import Team4.TobeHonest.exception.NoMemberException;
import Team4.TobeHonest.exception.NoSuchFriendException;
import Team4.TobeHonest.service.FriendService;
import Team4.TobeHonest.service.ItemService;
import Team4.TobeHonest.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final FriendService friendService;
    private final ItemService itemService;
    @GetMapping("{memberId}")
    @ResponseBody
    public String memberInformation(@PathVariable Long memberId) {
        return "" + memberId;
    }

    @GetMapping("/friends")
    //로그인은 인터셉터에서 처리 해 준다고 생각..
    public List<FriendWithSpecifyName> findAllFriends(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        /*Authentication 객체가 SecurityContext에 저장됩니다.
            그리고 이 SecurityContext는 HttpSession에 SPRING_SECURITY_CONTEXT라는 키로 저장*/

        //수정해야함 ==> member 엔티티가 controller에 노출되는것을 해결해보자

        String userEmail = userDetails.getUsername();
        Member member = (Member) request.getSession().getAttribute(userEmail);
        return friendService.findAllFriendsProfile(member);

    }

    //post로 수정..
    @GetMapping("/friends/add/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long friendId,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            HttpServletRequest request) {
        String userEmail = userDetails.getUsername();
        Member member = (Member) request.getSession().getAttribute(userEmail);
        friendService.addFriendList(member, friendId);

        return ResponseEntity.status(HttpStatus.OK).body("friend added");
    }

    @GetMapping("/friends/search/{startsWith}")
    public List<FriendWithSpecifyName> addFriend(@PathVariable String startsWith,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 HttpServletRequest request) {
        String userEmail = userDetails.getUsername();
        Member member = (Member) request.getSession().getAttribute(userEmail);
        //member argument 수정
        return friendService.searchFriendWithName(member, startsWith);

    }


    @DeleteMapping("/friends/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId,
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               HttpServletRequest request) {
        String userEmail = userDetails.getUsername();
        Member member = (Member) request.getSession().getAttribute(userEmail);
        friendService.deleteFriend(member, friendId);
        return ResponseEntity.status(HttpStatus.OK).body("delete success!");

    }


    @GetMapping("/search/phoneNumber/{phoneNumber}")
    @ResponseBody
    public MemberSearch findMemberByPhoneNumber(@PathVariable String phoneNumber) {
        return memberService.memberSearchByPhoneNumber(phoneNumber);
    }


    @GetMapping("/search/email/{email}")
    @ResponseBody
    public MemberSearch findMemberByEmail(@PathVariable String email) {
        return memberService.memberSearchByEmail(email);
    }

    @PostMapping("/points/add")
    @ResponseBody
    public void pointsAdd(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestBody Integer points,
                          HttpServletRequest request) {

        String memberEmail = userDetails.getUsername();
        memberService.pointRecharge(memberEmail, points);

    }

    @PostMapping("/points/use/{itemId}")
    @ResponseBody
    public ResponseEntity<String> pointsAdd(@AuthenticationPrincipal UserDetails userDetails,
                          @PathVariable Long itemId,
                          HttpServletRequest request) {
        String userEmail = userDetails.getUsername();
        Member member = (Member) request.getSession().getAttribute(userEmail);
        itemService.buyItem(member, itemId);
        return ResponseEntity.status(HttpStatus.OK).body("구매완료!");

    }


}
