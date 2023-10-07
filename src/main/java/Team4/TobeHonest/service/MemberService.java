package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.repo.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public void join(JoinDTO joinDTO) {
//      회원가입 중복 check
        if (memberRepository.findByEmail(joinDTO.getEmail()) != null) {
            throw new DuplicateMemberException("중복된 이메일입니다.");
        }
//      비밀번호값 암호화
        joinDTO.setPassWord(Member.hashPassword(joinDTO.getPassWord(), passwordEncoder));
        Member member = joinDTO.toMember();

        memberRepository.join(member);
    }

    //로그인 시 에러 메세지 출력하는 함수
    public String displayLoginError(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            FieldError fieldError = (FieldError) error;
            String message = error.getDefaultMessage();
            sb.append("field: ").append(fieldError.getField());
            sb.append("message: ").append(message);
        }
        return sb.toString();
    }

    //로그인
    public boolean login(String email, String passWord) {
        //Member을 return하지 않고 boolean을 return해서 controller에 entity가 노출되는것을 방지하자
        String hash = Member.hashPassword(passWord, passwordEncoder);
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            // 이메일이 존재하지 않는 경우, 예외 처리
            throw new UsernameNotFoundException("이메일이 존재하지 않습니다.");
        }

        if (!passwordEncoder.matches(passWord, member.getPassword())) {
            // 비밀번호가 일치하지 않는 경우, 예외 처리
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        return true;
    }

    public Member findByID(Long id) {
        return memberRepository.findById(id);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);

    }

    public Member findByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);

    }


}
