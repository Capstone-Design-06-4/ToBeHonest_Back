package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public Member loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if (member == null){
            throw new UsernameNotFoundException("can't find " + username);
        }
        return member;
    }

    //로그인한 맴버 객체 가져오기
    public Member getCurrentLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Member) {
            return (Member) authentication.getPrincipal();
        }
        return null;  // 혹은 적절한 예외를 던져 처리할 수 있습니다.
    }
}