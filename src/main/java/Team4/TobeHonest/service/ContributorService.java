package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.Contributor;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.QWishItem;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.FriendWishItemInfoDTO;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class ContributorService {

    private final WishItemRepository wishItemRepository;
    private final ContributorRepository contributorRepository;
    private final QWishItem wishItem = new QWishItem("wishItem");

    //    후원하기기능
//    로그인한 회원이 친구의 wishItem에 펀딩을 하는 기능..
    public void contributing(Member contributor, FriendWishItemInfoDTO wishItemInfoDTO,
                             Integer money) {
        Contributor contribution = contributorRepository
                .findContributionWithNamesById(contributor, wishItemInfoDTO.getFriendId(),
                        wishItemInfoDTO.getItemName());
//        내가 이미 펀딩했다면 그냥 값만 더하기
        if (contribution == null) {
            WishItem frindsWish = wishItemRepository.findWishItemByIdAndItemName(
                    wishItemInfoDTO.getFriendId(), wishItemInfoDTO.getItemName());
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

}
