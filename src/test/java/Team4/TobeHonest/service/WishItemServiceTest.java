package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.*;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.signup.JoinDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.FriendWishItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.exception.ItemNotInWishlistException;
import Team4.TobeHonest.repo.CategoryRepository;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

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

    @Before
    public void setUp() throws Exception {

        String[] data = {"아이폰", "갤럭시S", "나이키", "아이패드", "에어팟", "갤럭시탭", "비스포크냉장고", "비스포크청소기"};

        for (String datum : data) {
            JSONObject rjson = new JSONObject(search(datum));

            JSONArray items = rjson.getJSONArray("items");

            categoryProcess(items);
            for (int i = 0; i < items.length(); i++) {
                JSONObject itemJson = (JSONObject) items.get(i);
                if (itemJson.getInt("productType") != 1) {
                    continue;
                }
                ApiItemDTO itemDTO = new ApiItemDTO(itemJson);
                Category category = null;
                for (int j = 3; j >= 0; j--) {
                    String name = itemDTO.getCategoryList().get(j);
                    if (name.length() != 0) {
                        category = categoryRepository.findByName(name);
                        break;

                    }
                }
                Item item = new Item(itemDTO.getId(), itemDTO.getItemName(), itemDTO.getLprice(), itemDTO.getImage(), category);
                itemRepository.join(item);
            }

        }

        String email = "alswns2631@cau.ac.kr";
        String name = "choiminjun";
        String passWord = "passw123";
        String phoneNumber = "010-1234-1231";
        String birthDate = "19990803";
        JoinDTO joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        memberService.join(joinDTO);
        email = "alswns2631@gmail.com";
        name = "정상수";
        joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        memberService.join(joinDTO);
        email = "alswns2631@naver.com";
        name = "아이유";
        joinDTO = JoinDTO.builder().email(email).name(name).passWord(passWord).phoneNumber(phoneNumber).birthDate(birthDate).build();
        memberService.join(joinDTO);


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
        wishItemService.deleteWishList(this.member, DTO.getName());

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
        org.junit.jupiter.api.Assertions.assertThrows(ItemNotInWishlistException.class, () -> wishItemService.deleteWishList(this.member, nike.get(0).getName()));

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
            for (int i = 0; i < 3; i++) {
                FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(member.getId()).itemName(this.galaxy.get(i).getName()).friendName(member.getName()).categoryName(this.galaxy.get(i).getCategory().getName()).image(galaxy.get(i).getImage()).price(galaxy.get(i).getPrice()).build();
                contributorService.contributing(allFriend.getFriend(), friendWishItemInfoDTO, 10000);
            }

        }
        List<FirstWishItem> wishList = wishItemService.findWishList(this.member.getId());

        for (FirstWishItem firstWishItem : wishList) {

            Assertions.assertThat(images).contains(firstWishItem.getImage());
            Assertions.assertThat(firstWishItem.getPercentage()).isEqualTo(20000);
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
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(member.getId()).itemName(this.galaxy.get(0).getName()).friendName(member.getName()).categoryName(this.galaxy.get(0).getCategory().getName()).image(galaxy.get(0).getImage()).price(galaxy.get(0).getPrice()).build();
        for (FriendWith allFriend : allFriends) {
            contributorService.contributing(allFriend.getFriend(), friendWishItemInfoDTO, 10000);

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




    private String search(String query) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "Pi8zB3f4zuenOa7Lpdpl");
        headers.add("X-Naver-Client-Secret", "cB7nj_4ahS");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?" + "display=100" + "&filter=1" + "&exclude=used:rental:cbshop" + "&query=" + query, HttpMethod.GET, requestEntity, String.class);
        HttpStatusCode httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        return response;
    }


    private void categoryProcess(JSONArray items) {
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = (JSONObject) items.get(i);
            if (itemJson.getInt("productType") != 1) {
                continue;
            }
            Category[] c = new Category[4];
            ApiItemDTO itemDTO = new ApiItemDTO(itemJson);
            List<String> categoryList = itemDTO.getCategoryList();
            for (int j = 0; j < 4; j++) {
                {   //카테고리 존재 x
                    Category category;
                    if (categoryList.get(j).length() == 0) {
                        break;
                    }
                    if (categoryRepository.findByName(categoryList.get(j)) == null) {
                        category = new Category(categoryList.get(j));


                    } else {
                        category = categoryRepository.findByName(categoryList.get(j));
                    }

                    c[j] = category;
                    categoryRepository.join(category);
                }
            }
            for (int j = 1; j < 4; j++) {
                {   //카테고리 존재 x
                    if (c[j] == null) {
                        break;
                    }
                    c[j].setParent(c[j - 1]);
                }

            }


        }
    }
}
