package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.member.MemberDetailInformation;
import Team4.TobeHonest.dto.member.MemberSearch;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.enumer.FriendStatus;
import Team4.TobeHonest.enumer.IsThanksMessagedSend;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.exception.NoMemberException;
import Team4.TobeHonest.repo.FriendRepository;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final WishItemRepository wishItemRepository;



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


        Member member = memberRepository.findById(id);
        if (member == null) {
            throw new NoMemberException("회원 정보를 찾을 수 없습니다");
        }
        return member;
    }

    public Member findByEmail(String email) {

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new NoMemberException("회원 정보를 찾을 수 없습니다");
        }
        return member;
    }

    public Member findByEmailWithNoException(String email) {

        return memberRepository.findByEmail(email);
    }



    public Member findByPhoneNumber(String phoneNumber) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber);
        if (member == null) {
            throw new NoMemberException("회원 정보를 찾을 수 없습니다");
        }
        return member;

    }
    public String findByPhoneNumberRetEmail(String phoneNumber) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber);
        if (member == null) {
            return "";
        }
        return member.getEmail();

    }

    public MemberSearch memberSearchByEmail(String email) {
        Member member = this.findByEmail(email);

        return MemberSearch.builder()
                .memberId(member.getId())
                .profileImgURL(member.getProfileImg())
                .memberName(member.getName()).build();

    }

    public MemberSearch memberSearchByPhoneNumber(String phoneNumber) {
        Member member = this.findByPhoneNumber(phoneNumber);
        return MemberSearch.builder()
                .memberId(member.getId())
                .profileImgURL(member.getProfileImg())
                .memberName(member.getName()).build();

    }

    @Transactional
    public void joinMember(Member member) {


        memberRepository.join(member);
    }


    //돈 충전은 최고 높은 트랜잭션 격리수준 적용..
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer pointRecharge(String memberEmail, Integer money) {

        Member member = memberRepository.findByEmail(memberEmail);

        if (member == null){
            throw new NoMemberException(memberEmail + " not Found");
        }
        member.addPoints(money);
        return member.getPoints();
    }
    //돈 충전은 최고 높은 트랜잭션 격리수준 적용..
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer usePoints(String memberEmail, Integer money) {

        Member member = memberRepository.findByEmail(memberEmail);

        if (member == null){
            throw new NoMemberException(memberEmail + " not Found");
        }
        member.usePoints(money);
        return member.getPoints();
    }


    @Transactional
    public String changeProfileImg(MultipartFile file, Member member) throws IOException {
        String fileName = "profile/" + member.getId();
        ObjectMetadata metadata= new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        String encodedReturnURL = "https://" + bucket + ".s3.amazonaws.com/" + fileName;
        member.changeProfileImg(encodedReturnURL);
        return encodedReturnURL;

    }


    public MemberDetailInformation findMemberDetail(String memberEmail){
        Member member = memberRepository.findByEmail(memberEmail);

        List<FirstWishItem> wishItemUsed = wishItemRepository.findWishItemUsed(member.getId());

        int sendMessageSize = wishItemUsed.stream().filter(firstWishItem -> firstWishItem.getIsMessaged().equals(IsThanksMessagedSend.MESSAGED)).toList().size();

        return MemberDetailInformation.builder()
                .name(member.getName())
                .profileURL(member.getProfileImg())
                .birthDate(member.getBirthDate())
                .myPoints(member.getPoints())
                .progressNum(wishItemRepository.findWishItemInProgress(member.getId()).size())
                .completedNum(wishItemRepository.findWishItemCompleted(member.getId()).size())
                .usedMsgNum(sendMessageSize)
                .usedNoMsgNum(wishItemUsed.size() - sendMessageSize)
                .build();

    }


}
