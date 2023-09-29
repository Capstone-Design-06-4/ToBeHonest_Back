/*package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Category;
import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.dto.ApiItemDTO;

import Team4.TobeHonest.repo.CategoryRepository;
import Team4.TobeHonest.repo.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class testController {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final EntityManager em;

    @GetMapping("{itemName}")
    //로그인은 인터셉터에서 처리 해 준다고 생각..
    public void findFriends(@PathVariable String itemName, HttpServletRequest request) {
        JSONObject rjson = new JSONObject(search(itemName));
        JSONArray items = rjson.getJSONArray("items");

        categoryProcess(items);
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = (JSONObject) items.get(i);
            if (itemJson.getInt("productType") != 1) {
                continue;
            }
            ApiItemDTO itemDTO = new ApiItemDTO(itemJson);
            if (itemRepository.findByNaverId(itemDTO.getId()) != null) {
                log.error(itemRepository.findByNaverId(itemDTO.getId()).getName());
                continue;
            }
            Category category = null;
            for (int j = 3; j >= 0; j--) {
                String name = itemDTO.getCategoryList().get(j);
                if (name.length() != 0) {
                    category = categoryRepository.findByName(name);
                    break;

                }
            }
            log.error(category.getName());
            Item item = new Item(itemDTO.getId(), itemDTO.getItemName(), itemDTO.getLprice(), itemDTO.getImage(), category);
            itemRepository.join(item);

        }
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


    public String search(String query) {
        RestTemplate rest = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Naver-Client-Id", "Pi8zB3f4zuenOa7Lpdpl");
        headers.add("X-Naver-Client-Secret", "cB7nj_4ahS");
        String body = "";

        HttpEntity<String> requestEntity = new HttpEntity<String>(body, headers);
        ResponseEntity<String> responseEntity = rest.exchange("https://openapi.naver.com/v1/search/shop.json?" +
                        "display=100" +
                        "&filter=1" +
                        "&exclude=used:rental:cbshop" +
                        "&query=" + query,
                HttpMethod.GET, requestEntity, String.class);
        HttpStatusCode httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("Response status: " + status);
        System.out.println(response);
        return response;
    }
}*/
