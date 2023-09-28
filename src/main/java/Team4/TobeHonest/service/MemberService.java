package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.repo.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void join(Member member) {
//      회원가입 중복 check
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new DuplicateMemberException("중복된 이메일입니다.");
        }
//        db에 저장
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
    public Member login(String email, String passWord) {
        return memberRepository.loginFind(email, passWord);
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
