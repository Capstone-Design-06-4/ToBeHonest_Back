package Team4.TobeHonest.controller;


import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.service.ContributorService;
import Team4.TobeHonest.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/contribution")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ContributorController {
    private final MemberService memberService;
    private final ContributorService contributorService;


    @PostMapping("{wishItemId}")
    public ResponseEntity<String> contributing(@PathVariable Long wishItemId,
                                               @RequestBody Integer fundAmount,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Member member = memberService.findByEmail(userEmail);
        contributorService.contributing(member, wishItemId, fundAmount);
        return ResponseEntity.ok(fundAmount + "원 펀딩 완료!");


    }

}
