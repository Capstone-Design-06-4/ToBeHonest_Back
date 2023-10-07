package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.*;
import Team4.TobeHonest.dto.*;
import Team4.TobeHonest.repo.CategoryRepository;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import jakarta.persistence.EntityManager;
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
public class ContributorServiceTest {
    @Autowired
    public CategoryRepository categoryRepository;
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
    EntityManager em;
    private List<Item> galaxy;

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


        this.member = memberService.findByEmail("alswns2631@cau.ac.kr");
        this.friend1 = memberService.findByEmail("alswns2631@gmail.com");
        this.friend2 = memberService.findByEmail("alswns2631@naver.com");
        friendService.addFriendList(member, friend1);
        friendService.addFriendList(member, friend2);
        friendService.addFriendList(friend1, member);
        friendService.addFriendList(friend2, member);
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
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(friend.getId()).itemName(item.getName()).friendName(friend.getName()).categoryName(item.getCategory().getName()).image(item.getImage()).price(item.getPrice()).build();
        contributorService.contributing(me, friendWishItemInfoDTO, 10000);
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
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(friend.getId()).itemName(item.getName()).friendName(friend.getName()).categoryName(item.getCategory().getName()).image(item.getImage()).price(item.getPrice()).build();

        contributorService.contributing(me, friendWishItemInfoDTO, 10000);
        contributorService.contributing(me, friendWishItemInfoDTO, 10000);
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
        FriendWishItemInfoDTO friendWishItemInfoDTO = FriendWishItemInfoDTO.builder().friendId(member.getId()).itemName(this.galaxy.get(0).getName()).friendName(member.getName()).categoryName(this.galaxy.get(0).getCategory().getName()).image(galaxy.get(0).getImage()).price(galaxy.get(0).getPrice()).build();
        for (FriendWith allFriend : allFriends) {
            contributorService.contributing(allFriend.getFriend(), friendWishItemInfoDTO, 10000);

        }

        List<WishItem> all = wishItemRepository.findAll(member);
        Long wishItemId = all.get(0).getId();
        List<ContributorDTO> contributors = contributorService.findContributor(wishItemId);
        List<Long> cIds = contributors.stream().map(contributorDTO -> {
            return contributorDTO.getFriendId();
        }).toList();
        for (ContributorDTO contributor : contributors) {
            Assertions.assertThat(contributor.getContribution()).isEqualTo(10000);
            Assertions.assertThat(cIds).contains(contributor.getFriendId());
            Assertions.assertThat(contributor.getWishItemId()).isEqualTo(wishItemId);

        }

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
