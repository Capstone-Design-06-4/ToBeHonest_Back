package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.FriendWishItemInfoDTO;
import Team4.TobeHonest.dto.ItemInfoDTO;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.exception.ItemNotInWishlistException;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class WishItemService {

    private final WishItemRepository wishItemRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void addWishList(Member member, ItemInfoDTO itemInfoDTO){
        WishItem wishItem = wishItemRepository.findWishItemByItemName(member, itemInfoDTO.getName());
        if (wishItem != null){
            throw new DuplicateWishItemException("이미 위시리스트에 존재하는 아이템입니다!");
        }
        Item item = itemRepository.findByName(itemInfoDTO.getName());
        wishItem = WishItem.builder()
                        .item(item)
                                .fundMoney(item.getPrice())
                                        .member(member).build();
        wishItemRepository.join(wishItem);
    }
    @Transactional
    public void deleteWishList(Member member, String itemName) {
        WishItem wishItem =
                wishItemRepository.findWishItemByItemName(member, itemName);
        if (wishItem == null) {
            throw new ItemNotInWishlistException("해당 아이템은 위시리스트에 존재하지 않습니다.");
        }
        wishItemRepository.deleteWishItem(wishItem);
    }






}
