package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.exception.NoItemException;
import Team4.TobeHonest.repo.ItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemService {

    private final ItemRepository itemRepository;

    //검색
    public List<ItemInfoDTO> findItem(String name) {
        List<Item> item = itemRepository.searchItemName(name);
        List<ItemInfoDTO> result;
        result = entityToDTO(item);
        return result;
    }


    //검색
    public List<ItemInfoDTO> findCertainItem(String name) {
        List<Item> item = List.of(itemRepository.findByName(name));
        List<ItemInfoDTO> result;
        result = entityToDTO(item);
        return result;
    }


    public List<ItemInfoDTO> findCertainItemByCategory(String name) {

        List<Item> item = List.of(itemRepository.findByNameByCategory(name));
        List<ItemInfoDTO> result;
        result = entityToDTO(item);
        return result;
    }

    public List<ItemInfoDTO> findItemByCategory(String name) {

        List<Item> item = itemRepository.searchItemNameByCategory(name);
        List<ItemInfoDTO> result;
        result = entityToDTO(item);
        return result;
    }

    public ItemInfoDTO findByItembyID(Long itemID) {
        Item item = itemRepository.findByItem(itemID);
        if (item == null) {
            throw new NoItemException("DB에 아이템 정보가 없습니다");
        }
        return ItemInfoDTO.ItemToItemInfoDTO(item);
    }


    private List<ItemInfoDTO> entityToDTO(List<Item> item) {

        return item.stream().map(ItemInfoDTO::ItemToItemInfoDTO).toList();
        //Item에서 itemInfoDTO클래스로 변환한 후 controller에 return
    }
}



