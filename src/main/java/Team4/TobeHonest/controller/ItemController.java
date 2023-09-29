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

    @PostMapping("/find")
    public List<ItemInfoDTO> findItem(@RequestBody String name) {
        log.error(name);
        List<ItemInfoDTO> item = itemService.findItem(name);
        for (ItemInfoDTO itemInfoDTO : item) {
            log.error(itemInfoDTO.getName());

        }
        return itemService.findItem(name);

    }
    @GetMapping("/find")
    public String findItemForm(){
        return "물건찾는 폼 주세요";
    }
}
