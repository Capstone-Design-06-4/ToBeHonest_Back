package Team4.TobeHonest.controller;

import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.exception.NoItemException;
import Team4.TobeHonest.service.ItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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


    @GetMapping("{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        ItemInfoDTO itemInfoDTO;
        try{
         itemInfoDTO = itemService.findByItembyID(id);
        }
        catch (NoItemException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok(itemInfoDTO);

    }

    //아이템 검색
    @GetMapping("/search/{keyword}")
    public List<ItemInfoDTO> searchItem(@PathVariable String keyword) {
        return itemService.findItem(keyword);

    } //아이템 검색

    @GetMapping("/find/{name}")
    public List<ItemInfoDTO> findItem(@PathVariable String name) {
        return itemService.findCertainItem(name);

    }


    @GetMapping("/categories/find/{keyword}")
    public List<ItemInfoDTO> findItemByCategoryName(@PathVariable String keyword) {
        return itemService.findCertainItemByCategory(keyword);

    }

    @GetMapping("/categories/search/{keyword}")
    public List<ItemInfoDTO> searchItemByCategoryName(@PathVariable String keyword) {
        return itemService.findItemByCategory(keyword);

    }

}
