package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.Contributor;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.QWishItem;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.contributor.ContributorDTO;
import Team4.TobeHonest.exception.NoWishItemException;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ContributorService {

    private final WishItemRepository wishItemRepository;
    private final ContributorRepository contributorRepository;
    private final QWishItem wishItem = new QWishItem("wishItem");

    //    후원하기기능
//    로그인한 회원이 친구의 wishItem에 펀딩을 하는 기능..
    @Transactional
    public void contributing(Member contributor, Long wishItemId,
                             Integer money) {
        Contributor contribution = contributorRepository.findContributorsInWishItem(wishItemId, contributor);
//        내가 이미 펀딩했다면 그냥 값만 더하기
        if (contribution == null) {

            WishItem frindsWish = wishItemRepository.findWishItemById(wishItemId);
            if (frindsWish == null){
                throw new NoWishItemException("해당 위시아이템이 존재하지 않습니다");
            }
            contribution = Contributor.builder()
                    .contributor(contributor)
                    .fundMoney(money)
                    .fundDateTime(LocalDateTime.now())
                    .wishItem(frindsWish).build();
            contributorRepository.join(contribution);
        }
        else {
            contribution.addFundMoney(money);
        }
    }

    public List<ContributorDTO> findContributor(Long wishItemId){
        return contributorRepository.findContributorsInWishItem(wishItemId);
    }

    public List<Long> findGiftGiversToMe(Long myId){
        return contributorRepository.findAllContributors(myId);
    }

    public List<Long> getGiftReceiversFromMe(Long myId){
        return contributorRepository.findMyContributions(myId);
    }
}
