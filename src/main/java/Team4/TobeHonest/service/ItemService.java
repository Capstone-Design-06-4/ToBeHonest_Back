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
    //검색
    public List<ItemInfoDTO> findItem(String name){
        List<Item> item = itemRepository.searchItemName(name);
        List<ItemInfoDTO> result = new ArrayList<>();
        entityToDTO(item, result);
        return result;
    }


    //검색
    public List<ItemInfoDTO> findCertainItem(String name){
        List<Item> item = List.of(itemRepository.findByName(name));
        List<ItemInfoDTO> result = new ArrayList<>();
        entityToDTO(item, result);
        return result;
    }


    public List<ItemInfoDTO> findCertainItemByCategory(String name){

        List<Item> item = List.of(itemRepository.findByNameByCategory(name));
        List<ItemInfoDTO> result = new ArrayList<>();
        entityToDTO(item, result);
        return result;
    }

    public List<ItemInfoDTO> findItemByCategory(String name){

        List<Item> item = itemRepository.searchItemNameByCategory(name);
        List<ItemInfoDTO> result = new ArrayList<>();
        entityToDTO(item, result);
        return result;
    }



    private void entityToDTO(List<Item> item, List<ItemInfoDTO> result) {
        //Item에서 itemInfoDTO클래스로 변환한 후 controller에 return
        for (Item item1 : item) {
            ItemInfoDTO itemInfoDTO = ItemInfoDTO.ItemToItemInfoDTO(item1);
            result.add(itemInfoDTO);
        }
    }
}
