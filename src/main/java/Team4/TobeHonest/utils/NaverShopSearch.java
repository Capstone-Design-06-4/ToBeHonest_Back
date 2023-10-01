/*
package Team4.TobeHonest.utils;

import Team4.TobeHonest.dto.ItemDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class NaverShopSearch {


    public NaverShopSearch() {
    }

    public String search(String query) {
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
        System.out.println("Response status: " + status);
        System.out.println(response);
        return response;
    }

    public static void main(String[] args) {

        NaverShopSearch naverShopSearch = new NaverShopSearch();



        naverShopSearch.search("아이폰");
        naverShopSearch.search("갤럭시S");
        naverShopSearch.search("아이패드");
        naverShopSearch.search("갤럭시 폴드");
        naverShopSearch.search("갤럭시탭");
        naverShopSearch.search("에어팟");
        naverShopSearch.search("갤럭시 버즈");
        naverShopSearch.search("맥북");
        naverShopSearch.search("갤럭시 북");


    }
}*/
