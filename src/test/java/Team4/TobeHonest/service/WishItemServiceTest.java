package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Category;
import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.ApiItemDTO;
import Team4.TobeHonest.dto.ItemInfoDTO;
import Team4.TobeHonest.dto.JoinDTO;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.exception.ItemNotInWishlistException;
import Team4.TobeHonest.repo.CategoryRepository;
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


    //미리 아이템 저장하기.
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
        JoinDTO joinDTO = JoinDTO.builder().
                email(email).
                name(name).
                passWord(passWord).
                phoneNumber(phoneNumber).
                birthDate(birthDate).
                build();
        Member member = joinDTO.toMember();
        memberService.join(joinDTO);
    }





    @Test
    @DisplayName("위시 아이템추가하기 -정상작동")
    public void addDeleteWishItem(){
        //로그인
        Member member = memberService.findByEmail("alswns2631@cau.ac.kr");
        List<Item> galaxy = itemRepository.searchItemName("갤럭시");
        System.out.println(galaxy.size());
        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(galaxy.get(0));
        //위시아이템에 추가
        wishItemService.addWishList(member, DTO);

        Assertions.assertThat(wishItemRepository.
                findWishItemByItemName(member, galaxy.get(0).getName()).getItem().getName()).
                isEqualTo(galaxy.get(0).getName());
        Assertions.assertThat(wishItemRepository.findAll(member).size()).isEqualTo(1);

        //위시아이템 삭제
        wishItemService.deleteWishList(member, DTO.getName());

        Assertions.assertThat(wishItemRepository.findAll(member).size()).isEqualTo(0);
    }


    @Test
    @DisplayName("위시 아이템 추가하기 -에러작동")
    public void checkAddWishItemError(){
        Member member = memberService.findByEmail("alswns2631@cau.ac.kr");
        List<Item> galaxy = itemRepository.searchItemName("갤럭시");
        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(galaxy.get(0));
        //위시아이템에 중복추가
        wishItemService.addWishList(member, DTO);
        org.junit.jupiter.api.Assertions.assertThrows(DuplicateWishItemException.class,
                ()-> wishItemService.addWishList(member, DTO));

    }

    @Test
    @DisplayName("위시 아이템 추가하기 -에러작동")
    public void checkDeleteWishItemError() {
        Member member = memberService.findByEmail("alswns2631@cau.ac.kr");
        List<Item> galaxy = itemRepository.searchItemName("갤럭시");
        ItemInfoDTO DTO = ItemInfoDTO.ItemToItemInfoDTO(galaxy.get(0));
        List<Item> nike = itemRepository.searchItemName("나이키");

        wishItemService.addWishList(member, DTO);
        DTO = ItemInfoDTO.ItemToItemInfoDTO(nike.get(0));
        org.junit.jupiter.api.Assertions.assertThrows(ItemNotInWishlistException.class
                ,()->wishItemService.deleteWishList(member, nike.get(0).getName()));

    }


    
    
    private String search(String query) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "Pi8zB3f4zuenOa7Lpdpl");
        headers.add("X-Naver-Client-Secret", "cB7nj_4ahS");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?" +
                        "display=100"+
                        "&filter=1" +
                        "&exclude=used:rental:cbshop" +
                        "&query=" + query,
                HttpMethod.GET, requestEntity, String.class);
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
