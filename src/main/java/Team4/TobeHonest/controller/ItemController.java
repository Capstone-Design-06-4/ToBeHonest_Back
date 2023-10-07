package Team4.TobeHonest.controller;

import Team4.TobeHonest.dto.ItemInfoDTO;
import Team4.TobeHonest.service.ItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ItemController {

    private final ItemService itemService;


    //아이템 검색
    @PostMapping("/search/{keyword}")
    public List<ItemInfoDTO> searchItem(@PathVariable String keyword) {
        return itemService.findItem(keyword);

    } //아이템 검색
    @PostMapping("/find/{name}")
    public List<ItemInfoDTO> findItem(@PathVariable String name) {
        return itemService.findCertainItem(name);

    }

    @PostMapping("/categories/find/{keyword}")
    public List<ItemInfoDTO> findItemByCategoryName(@PathVariable String keyword) {
        return itemService.findCertainItemByCategory(keyword);

    }
    @PostMapping("/categories/search/{keyword}")
    public List<ItemInfoDTO> searchItemByCategoryName(@PathVariable String keyword) {
        return itemService.findItemByCategory(keyword);

    }

}
