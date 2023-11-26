package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.friendWIth.FriendWithSpecifyName;
import Team4.TobeHonest.dto.member.MemberDetailInformation;
import Team4.TobeHonest.dto.member.MemberSearch;
import Team4.TobeHonest.enumer.FriendStatus;
import Team4.TobeHonest.service.FriendService;
import Team4.TobeHonest.service.ItemService;
import Team4.TobeHonest.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final FriendService friendService;
    private final ItemService itemService;


    //로그인한 회원의 디테일 정보
    @GetMapping("/detail-information")
    public MemberDetailInformation findLoginMemberDetails(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return memberService.findMemberDetail(userEmail);
    }

    @GetMapping("/friends")
    //로그인은 인터셉터에서 처리 해 준다고 생각..
    public List<FriendWithSpecifyName> findAllFriends(@AuthenticationPrincipal UserDetails userDetails) {
        /*Authentication 객체가 SecurityContext에 저장됩니다.
            그리고 이 SecurityContext는 HttpSession에 SPRING_SECURITY_CONTEXT라는 키로 저장*/

        //수정해야함 ==> member 엔티티가 controller에 노출되는것을 해결해보자

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);


        return friendService.findAllFriendsProfile(member);

    }

    //post로 수정..
    @PostMapping("/friends/add/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long friendId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        FriendWithSpecifyName friendWithSpecifyName = friendService.addFriendList(member, friendId);

        return ResponseEntity.status(HttpStatus.OK).body(friendWithSpecifyName);
    }

    @GetMapping("/friends/search/{startsWith}")
    public List<FriendWithSpecifyName> searchFriends(@PathVariable String startsWith,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        //member argument 수정
        return friendService.searchFriendWithName(member, startsWith);

    }

    @GetMapping("/friends/searchId/{startsWith}")
    public List<Long> searchFriendIds(@PathVariable String startsWith,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        //member argument 수정
        return friendService.searchFriendWithNameOnlyFriendIdReturn(member, startsWith);

    }


    @DeleteMapping("/friends/delete/{friendId}")
    public ResponseEntity<String> deleteFriend(@PathVariable Long friendId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);

        friendService.deleteFriend(member, friendId);
        return ResponseEntity.status(HttpStatus.OK).body("delete success!");

    }


    @GetMapping("/search/phoneNumber/{phoneNumber}")
    @ResponseBody
    public MemberSearch findMemberByPhoneNumber(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String phoneNumber) {
        String memberEmail = userDetails.getUsername();
        String email = memberService.findByPhoneNumberRetEmail(phoneNumber);
        FriendStatus friendStatus = findFriendStatus(email, memberEmail);
        if (friendStatus == FriendStatus.EMPTY) {
            return MemberSearch.builder().friendStatus(friendStatus).build();
        }
        MemberSearch memberSearch = memberService.memberSearchByEmail(email);
        memberSearch.setFriendStatus(friendStatus);
        return memberSearch;
    }


    @GetMapping("/search/email/{email}")
    @ResponseBody
    public MemberSearch findMemberByEmail(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable String email) {

        String memberEmail = userDetails.getUsername();
        FriendStatus friendStatus = findFriendStatus(email, memberEmail);
        if (friendStatus == FriendStatus.EMPTY) {
            return MemberSearch.builder().friendStatus(friendStatus).build();
        }
        MemberSearch memberSearch = memberService.memberSearchByEmail(email);
        memberSearch.setFriendStatus(friendStatus);
        return memberSearch;
    }

    @PostMapping("/points/add")
    @ResponseBody
    public void pointsAdd(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestBody Integer points) {

        String memberEmail = userDetails.getUsername();
        memberService.pointRecharge(memberEmail, points);

    }

    @PostMapping("/points/use/{itemId}")
    @ResponseBody
    public ResponseEntity<String> pointsUse(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long itemId) {
        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        itemService.buyItem(member, itemId);
        return ResponseEntity.status(HttpStatus.OK).body("구매완료!");

    }


    @PostMapping("/changeProfileImg")
    public ResponseEntity<?> changeProfileImg(@RequestParam MultipartFile file,
                                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        String returnURL = memberService.changeProfileImg(file, member);
        return ResponseEntity.status(HttpStatus.OK).body(returnURL);
    }

    private FriendStatus findFriendStatus(String searchEmail, String loginEmail) {
        Member member = memberService.findByEmailWithNoException(searchEmail);
        Member loginMember = memberService.findByEmail(loginEmail);
        if (member == null) {
            return FriendStatus.EMPTY;
        } else if (member == loginMember) {
            return FriendStatus.ME;
        } else if (friendService.isFriend(loginMember, member)) {
            return FriendStatus.FRIEND;
        }
        return FriendStatus.NOT_FRIEND;
    }


    @GetMapping("/myExpected")
    public ResponseEntity<Integer> myExpectedAmount(@AuthenticationPrincipal UserDetails userDetails) {
        Integer myExpected = memberService.findMyExpected(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(myExpected);
    }
}
