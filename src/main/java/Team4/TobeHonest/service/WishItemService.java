package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.exception.ItemNotInWishlistException;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.MemberRepository;
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
    private final MemberRepository memberRepository;

    @Transactional
    public void addWishList(Member member, ItemInfoDTO itemInfoDTO) {
        WishItem wishItem = wishItemRepository.findWishItemByItemId(member, itemInfoDTO.getId());
        if (wishItem != null) {
            throw new DuplicateWishItemException("이미 위시리스트에 존재하는 아이템입니다!");
        }
        Item item = itemRepository.findByName(itemInfoDTO.getName());
        wishItem = WishItem.builder()
                .item(item)
                .money(item.getPrice())
                .member(member).build();
        wishItemRepository.join(wishItem);

    }

    @Transactional
    public void deleteWishListByItemName(Member member, String itemName) {
        WishItem wishItem =
                wishItemRepository.findWishItemByItemName(member, itemName);
        if (wishItem == null) {
            throw new ItemNotInWishlistException("해당 아이템은 위시리스트에 존재하지 않습니다.");
        }
        wishItemRepository.deleteWishItem(wishItem);
    }

    @Transactional
    public void deleteWishListByItemId(Member member, Long itemId) {
        WishItem wishItem =
                wishItemRepository.findWishItemByItemId(member, itemId);
        if (wishItem == null) {
            throw new ItemNotInWishlistException("해당 아이템은 위시리스트에 존재하지 않습니다.");
        }
        wishItemRepository.deleteWishItem(wishItem);
    }




    public List<FirstWishItem> findAllWishList(Long memberId){
        Member member = memberRepository.findById(memberId);
        return wishItemRepository.findFirstWishList(member);
    }

    public List<FirstWishItem> findWishListInProgress(Member member){
        return wishItemRepository.findWishItemInProgress(member.getId());

    }

    public List<FirstWishItem> findWishListCompleted(Member member){
        return wishItemRepository.findWishItemCompleted(member.getId());

    }

    public List<FirstWishItem> findWishListUsed(Member member){
        return wishItemRepository.findWishItemUsed(member.getId());

    }

    public List<WishItemDetail> findWishItemDetail(Long wishItemId){
        return wishItemRepository.findWishItemDetail(wishItemId);
    }

}
