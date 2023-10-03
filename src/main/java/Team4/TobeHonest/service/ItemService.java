package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.dto.ItemInfoDTO;
import Team4.TobeHonest.repo.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access =  AccessLevel.PROTECTED)
public class ItemService {

    private final ItemRepository itemRepository;

    public List<ItemInfoDTO> findItem(String string){
        List<Item> item = itemRepository.searchItemName(string);
        List<ItemInfoDTO> result = new ArrayList<>();

        //Item에서 itemInfoDTO클래스로 변환한 후 controller에 return
        for (Item item1 : item) {
            ItemInfoDTO itemInfoDTO = ItemInfoDTO.ItemToItemInfoDTO(item1);
            result.add(itemInfoDTO);
        }

        return result;
    }
}
