package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.repo.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public void join(Member member) {
//      중복이존재
        if (memberRepository.getMemberByEmail(member.getEmail()) != null) {
            throw new DuplicateMemberException("중복된 이메일입니다.");
        }
        memberRepository.join(member);
    }

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

    public Member login(String email, String passWord) {
        return memberRepository.loginFind(email, passWord);
    }
}
