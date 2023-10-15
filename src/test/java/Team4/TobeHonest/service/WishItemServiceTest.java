package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.FriendWith;
import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.exception.ItemNotInWishlistException;
import Team4.TobeHonest.repo.CategoryRepository;
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

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Transactional
public class WishItemServiceTest {
    @Autowired
    public WishItemService wishItemService;
    @Autowired
    public MemberService memberService;
    @Autowired
    public WishItemRepository wishItemRepository;
    @Autowired
    public CategoryRepository categoryRepository;
    @Autowired
    public ItemRepository itemRepository;

    public Member member;
    public Member friend1;
    public Member friend2;
    public List<Item> galaxy;

    @Autowired
    ContributorService contributorService;
    @Autowired
    ContributorRepository contributorRepository;

    @Autowired
    FriendService friendService;

    @Autowired
    NaverSearchService naverSearchService;
    @Autowired
    FriendJoinService friendJoinService;

    @Before
    public void setUp() throws Exception {

        naverSearchService.saveItemInDB();
        friendJoinService.saveMemberInDB();

        this.galaxy = itemRepository.searchItemName("갤럭시");
        this.member = memberService.findByEmail("alswns2631@cau.ac.kr");
        this.friend1 = memberService.findByEmail("alswns2631@gmail.com");
        this.friend2 = memberService.findByEmail("alswns2631@naver.com");
        friendService.addFriendList(member, friend1);
        friendService.addFriendList(member, friend2);
        friendService.addFriendList(friend1, member);
        friendService.addFriendList(friend2, member);

    }


    @Test
    @DisplayName("위시 아이템추가하기 -정상작동")
    public void addDeleteWishItem() {
        //로그인

        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        //위시아이템에 추가
        wishItemService.addWishList(this.member, DTO);

        Assertions.assertThat(wishItemRepository.findWishItemByItemName(this.member, this.galaxy.get(0).getName()).getItem().getName()).isEqualTo(galaxy.get(0).getName());
        Assertions.assertThat(wishItemRepository.findAll(this.member).size()).isEqualTo(1);

        //위시아이템 삭제
        wishItemService.deleteWishListByItemName(this.member, DTO.getName());

        Assertions.assertThat(wishItemRepository.findAll(this.member).size()).isEqualTo(0);
    }


    @Test
    @DisplayName("위시 아이템 추가하기 - 이미 위시 리스트에 있는 제품")
    public void checkAddWishItemError() {
        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(galaxy.get(0));
        //위시아이템에 중복추가
        wishItemService.addWishList(this.member, DTO);
        org.junit.jupiter.api.Assertions.assertThrows(DuplicateWishItemException.class, () -> wishItemService.addWishList(this.member, DTO));

    }

    @Test
    @DisplayName("위시 아이템 추가하기 -위시 리스트에 없는 item")
    public void checkDeleteWishItemError() {

        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        List<Item> nike = itemRepository.searchItemName("나이키");

        wishItemService.addWishList(this.member, DTO);
        DTO = ItemInfoDTO.ItemToItemInfoDTO(nike.get(0));
        org.junit.jupiter.api.Assertions.assertThrows(ItemNotInWishlistException.class, () -> wishItemService.deleteWishListByItemName(this.member, nike.get(0).getName()));

    }

    //    위시리스트 첫페이지 test
    @Test
    @DisplayName("우리가 위시리스트 화면 최초로 들어가면 나오는 data -사진, 퍼센티지(일단 토탈 투자받은 금액..)")
    public void testFirstWishList() {
        ItemInfoDTO g1 = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        ItemInfoDTO g2 = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(1));
        ItemInfoDTO g3 = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(2));

        wishItemService.addWishList(this.member, g1);
        wishItemService.addWishList(this.member, g2);
        wishItemService.addWishList(this.member, g3);

        List<String> images = new ArrayList<>();
        images.add(g1.getImage());
        images.add(g2.getImage());
        images.add(g3.getImage());

        List<FriendWith> allFriends = friendService.findAllFriends(member);
        for (FriendWith allFriend : allFriends) {
            List<WishItem> all = wishItemRepository.findAll(member);

            for (WishItem wishItem : all) {
                contributorService.contributing(allFriend.getFriend(), wishItem.getId(), 10000);
            }

        }
        List<FirstWishItem> wishList = wishItemService.findWishList(this.member.getId());

        for (FirstWishItem firstWishItem : wishList) {

            Assertions.assertThat(images).contains(firstWishItem.getImage());
            Assertions.assertThat(firstWishItem.getFundAmount()).isEqualTo(20000);
        }
        //friend가 투자한 상황..


    }

    @Test
    @DisplayName("우리가 위시리스트 화면 최초로 들어가면 나오는 data -사진, 퍼센티지(일단 토탈 투자받은 금액..)")
    public void testWishItemDetail() {
        ItemInfoDTO g1 = ItemInfoDTO.ItemToItemInfoDTO(this.galaxy.get(0));
        wishItemService.addWishList(this.member, g1);
//        멤버가 wishItem넣은 상황
        List<FriendWith> allFriends = friendService.findAllFriends(member);

        for (FriendWith allFriend : allFriends) {
            List<WishItem> all = wishItemRepository.findAll(member);

            for (WishItem wishItem : all) {
                contributorService.contributing(allFriend.getFriend(), wishItem.getId(), 10000);
            }

        }
//        친구들이 만원 씩 투자

        List<WishItem> all = wishItemRepository.findAll(member);
        Long wishItemId = all.get(0).getId();
        List<WishItemDetail> wishItemDetail = wishItemService.findWishItemDetail(wishItemId);
        WishItemDetail detail = wishItemDetail.get(0);


        Assertions.assertThat(detail.getWishItemId()).isEqualTo(wishItemId);
        Assertions.assertThat(detail.getItemName()).isEqualTo(this.galaxy.get(0).getName());
        Assertions.assertThat(detail.getFund()).isEqualTo(20000);
        Assertions.assertThat(detail.getTotal()).isEqualTo(this.galaxy.get(0).getPrice());
        Assertions.assertThat(detail.getImage()).isEqualTo(this.galaxy.get(0).getImage());


    }


}
