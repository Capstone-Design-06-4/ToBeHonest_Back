package Team4.TobeHonest.controller;


import Team4.TobeHonest.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class PointsController {


    private MemberService memberService;
}
