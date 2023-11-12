package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.contributor.ContributorDTO;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.FriendWishItemInfoDTO;
import Team4.TobeHonest.enumer.GiftStatus;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import Team4.TobeHonest.setup.FriendJoinService;
import Team4.TobeHonest.setup.NaverSearchService;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
public class ContributorServiceTest {

    @Autowired
    public ItemRepository itemRepository;
    public Member member;
    public Member friend1;
    public Member friend2;
    @Autowired
    MemberService memberService;
    @Autowired
    ContributorService contributorService;
    @Autowired
    ContributorRepository contributorRepository;
    @Autowired
    WishItemRepository wishItemRepository;
    @Autowired
    FriendService friendService;
    @Autowired
    WishItemService wishItemService;
    @Autowired
    NaverSearchService naverSearchService;
    @Autowired
    FriendJoinService friendJoinService;
    private List<Item> galaxy;

    @Before
    public void setUp() throws Exception {
        naverSearchService.saveItemInDB();
        friendJoinService.saveMemberInDB();


        this.member = memberService.findByEmail("alswns2631@cau.ac.kr");
        this.friend1 = memberService.findByEmail("alswns2631@gmail.com");
        this.friend2 = memberService.findByEmail("alswns2631@naver.com");
        friendService.addFriendList(member, friend1.getId());
        friendService.addFriendList(member, friend2.getId());
        friendService.addFriendList(friend1, member.getId());
        friendService.addFriendList(friend2, member.getId());
        this.galaxy = itemRepository.searchItemName("갤럭시");
        Item item = galaxy.get(0);
        Member member = memberService.findByEmail("alswns2631@gmail.com");
        WishItem wishItem = WishItem.builder().item(item).money(item.getPrice()).member(member).build();
        wishItemRepository.join(wishItem);
    }


    @Test
    @DisplayName("친구 위시리스트에 funding하기 - 정상작동")
    public void testFunding() {
        Member friend = memberService.findByEmail("alswns2631@gmail.com");
        Member me = memberService.findByEmail("alswns2631@cau.ac.kr");
        WishItem wishItem = wishItemRepository.findAll(friend).get(0);
        Item item = wishItem.getItem();
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(friend.getId()).itemName(item.getName()).categoryName(item.getCategory().getName()).image(item.getImage()).price(item.getPrice()).build();
        contributorService.contributing(me, wishItem.getId(), 10000);
        Contributor contributionWithNames = contributorRepository.findContributionWithNamesById(me, friend.getId(), item.getName());
        Assertions.assertThat(contributionWithNames.getContributor()).isEqualTo(me);
        Assertions.assertThat(contributionWithNames.getFundMoney()).isEqualTo(10000);
        Assertions.assertThat(contributionWithNames.getWishItem()).isEqualTo(wishItem);


    }

    @Test
    @DisplayName("친구 위시리스트에 funding하기 - 중복 fund작동확인")
    public void testDuplicateFunding() {
        Member friend = memberService.findByEmail("alswns2631@gmail.com");
        Member me = memberService.findByEmail("alswns2631@cau.ac.kr");
        WishItem wishItem = wishItemRepository.findAll(friend).get(0);
        Item item = wishItem.getItem();
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(friend.getId()).itemName(item.getName()).categoryName(item.getCategory().getName()).image(item.getImage()).price(item.getPrice()).build();

        contributorService.contributing(me, wishItem.getId(), 10000);
        contributorService.contributing(me, wishItem.getId(), 10000);
        Contributor contributionWithNames = contributorRepository.findContributionWithNamesById(me, friend.getId(), item.getName());
        Assertions.assertThat(contributionWithNames.getContributor()).isEqualTo(me);
        Assertions.assertThat(contributionWithNames.getFundMoney()).isEqualTo(20000);
        Assertions.assertThat(contributionWithNames.getWishItem()).isEqualTo(wishItem);

    }

    @Test
    @DisplayName("contribution 한 친구들 List 다 찾아오기..")
    public void testFindContributor() {
        ItemInfoDTO g1 = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        wishItemService.addWishList(this.member, g1);
        List<FriendWith> allFriends = friendService.findAllFriends(member);
        WishItem wishItem = wishItemRepository.findAll(member).get(0);
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(member.getId()).itemName(this.galaxy.get(0).getName()).categoryName(this.galaxy.get(0).getCategory().getName()).image(galaxy.get(0).getImage()).price(galaxy.get(0).getPrice()).build();
        for (FriendWith allFriend : allFriends) {
            contributorService.contributing(allFriend.getFriend(), wishItem.getId(), 10000);

        }

        List<WishItem> all = wishItemRepository.findAll(member);
        Long wishItemId = all.get(0).getId();
        List<ContributorDTO> contributors = contributorService.findContributor(wishItemId);
        List<Long> cIds = contributors.stream().map(ContributorDTO::getFriendId).toList();
        for (ContributorDTO contributor : contributors) {
            Assertions.assertThat(contributor.getContribution()).isEqualTo(10000);
            Assertions.assertThat(cIds).contains(contributor.getFriendId());
            Assertions.assertThat(contributor.getWishItemId()).isEqualTo(wishItemId);

        }

    }

    @Test
    @DisplayName("나에게 준사람들 전부 받아오기..")
    public void findMyAllContribution() {
        Member member = memberService.findByEmail("alswns2631@gmail.com");
        Member friend1 = memberService.findByEmail("alswns2631@cau.ac.kr");
        Member friend2 = memberService.findByEmail("alswns2631@naver.com");
        WishItem wishItem = wishItemRepository.findAll(member).get(0);
        contributorService.contributing(friend1, wishItem.getId(), 20000);
        contributorService.contributing(friend2, wishItem.getId(), 30000);
        List<Long> giftGiversToMe = contributorService.findGiftGiversToMe(member.getId());
        for (Long l : giftGiversToMe) {
            System.out.println(l);
        }
        Assertions.assertThat(giftGiversToMe).contains(friend1.getId());
        Assertions.assertThat(giftGiversToMe).contains(friend2.getId());

    }




    @Test
    @DisplayName("내가 준 사람")
    public void findMyContribution() {
        Member member = memberService.findByEmail("alswns2631@gmail.com");
        Member friend1 = memberService.findByEmail("alswns2631@cau.ac.kr");
        Member friend2 = memberService.findByEmail("alswns2631@naver.com");

        Item item = galaxy.get(3);
        WishItem wishItem = WishItem.builder().item(item).money(item.getPrice()).member(friend1).build();
        wishItemRepository.join(wishItem);
        item = galaxy.get(4);
        wishItem = WishItem.builder().item(item).money(item.getPrice()).member(friend2).build();
        wishItemRepository.join(wishItem);
        wishItem = wishItemRepository.findAll(friend1).get(0);
        contributorService.contributing(member, wishItem.getId(), 20000);
        wishItem = wishItemRepository.findAll(friend2).get(0);
        contributorService.contributing(member, wishItem.getId(), 30000);
        List<Long> giftReceiversFromMe = contributorService.getGiftReceiversFromMe(member.getId());

        Assertions.assertThat(giftReceiversFromMe).contains(friend1.getId());
        Assertions.assertThat(giftReceiversFromMe).contains(friend2.getId());

    }


    @Test
    @DisplayName("펀딩을 했을 때, 자동으로 상태가 변경되는가 확인")
    public void contributionChange(){
        Item item = galaxy.get(3);
        WishItem wishItem = WishItem.builder().item(item).money(item.getPrice()).member(member).build();
        wishItemRepository.join(wishItem);

        Assertions.assertThat(wishItem.getGiftStatus()).isEqualTo(GiftStatus.IN_PROGRESS);


        Integer price = item.getPrice();
        contributorService.contributing(friend1, wishItem.getId(), (Integer) (price/2) + 1);
        contributorService.contributing(friend2, wishItem.getId(), (Integer) (price/2) + 1);

        Assertions.assertThat(wishItem.getGiftStatus()).isEqualTo(GiftStatus.COMPLETED);
    }



}
