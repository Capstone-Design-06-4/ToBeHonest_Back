package Team4.TobeHonest.setup;

import Team4.TobeHonest.domain.Category;
import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.dto.ApiItemDTO;
import Team4.TobeHonest.repo.CategoryRepository;
import Team4.TobeHonest.repo.ItemRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NaverSearchService {

    @Autowired
    public CategoryRepository categoryRepository;

    @Autowired
    public ItemRepository itemRepository;

    public void saveItemInDB() {
        String[] data = {"아이폰", "갤럭시S", "나이키", "아이패드", "에어팟", "갤럭시탭", "비스포크냉장고", "비스포크청소기"};

        for (String datum : data) {
            JSONObject rjson = new JSONObject(this.search(datum));

            JSONArray items = rjson.getJSONArray("items");

            this.categoryProcess(items);
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
    }
    public String search(String query) {
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

    public void categoryProcess(JSONArray items) {
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
                    if (categoryList.get(j).isEmpty()) {
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