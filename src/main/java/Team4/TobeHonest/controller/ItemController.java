package Team4.TobeHonest.controller;

import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.service.ItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ItemController {

    private final ItemService itemService;

    //id값으로 item정보 조회하기
    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        ItemInfoDTO itemInfoDTO;
        itemInfoDTO = itemService.findByItembyID(id);
        return ResponseEntity.ok(itemInfoDTO);

    }

    //아이템 검색 ==> string으로
    @GetMapping("/search/{keyword}")
    public List<ItemInfoDTO> searchItem(@PathVariable String keyword) {
        return itemService.findItem(keyword);

    }

    //검색을 해도 잘안된다..
    @GetMapping("/categories/search/{keyword}")
    public List<ItemInfoDTO> searchItemByCategoryName(@PathVariable String keyword) {
        return itemService.findItemByCategory(keyword);

    }

}
