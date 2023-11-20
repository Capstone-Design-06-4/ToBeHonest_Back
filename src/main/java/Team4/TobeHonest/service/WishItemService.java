package Team4.TobeHonest.service;

import Team4.TobeHonest.domain.Item;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.domain.WishItem;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.enumer.GiftStatus;
import Team4.TobeHonest.exception.*;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.ItemRepository;
import Team4.TobeHonest.repo.MemberRepository;
import Team4.TobeHonest.repo.WishItemRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class WishItemService {

    private final WishItemRepository wishItemRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ContributorRepository contributorRepository;

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


    @Transactional
    public void deleteWIshItem(Long wishItemId){
        WishItem wishItem = wishItemRepository.findWishItemById(wishItemId);
        if (wishItem== null){
            throw new NoWishItemException("해당 아이템은 위시리스트에 존재하지 않습니다.");
        }
        wishItemRepository.deleteWishItem(wishItem);

    }

    public List<FirstWishItem> findAllWishList(Long memberId) {
        Member member = memberRepository.findById(memberId);
        List<FirstWishItem> firstWishList = wishItemRepository.findFirstWishList(member);
        setFundedAmount(firstWishList);
        return firstWishList;

    }

    public List<FirstWishItem> findWishListInProgress(Long memberId) {
        List<FirstWishItem> firstWishList = wishItemRepository.findWishItemInProgress(memberId);
        setFundedAmount(firstWishList);
        return firstWishList;
    }

    private void setFundedAmount(List<FirstWishItem> firstWishList) {
        firstWishList.forEach(i -> i.setFundAmount(
                contributorRepository.findFundedAmount(wishItemRepository.findWishItemById(i.getWishItemId()))));
    }


    public List<FirstWishItem> findWishListCompleted(Long memberId) {
        List<FirstWishItem> firstWishList = wishItemRepository.findWishItemCompleted(memberId);
        setFundedAmount(firstWishList);
        return firstWishList;
    }

    public List<FirstWishItem> findWishListUsed(Long memberId) {

        List<FirstWishItem> firstWishList = wishItemRepository.findWishItemUsed(memberId);
        setFundedAmount(firstWishList);
        return firstWishList;


    }


    public List<WishItemDetail> findWishItemDetail(Long wishItemId) {
        List<WishItemDetail> wishItemDetail = wishItemRepository.findWishItemDetail(wishItemId);
        wishItemDetail.forEach(i -> i.setFund(
                contributorRepository.findFundedAmount(wishItemRepository.findWishItemById(i.getWishItemId()))));
        return wishItemDetail;

    }


    //wishItem사용하기
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer useWishItem(String memberEmail, Long wishItemId) {
        WishItem wishItem = wishItemRepository.findWishItemById(wishItemId);
        if (!wishItem.getMember().getEmail().equals(memberEmail)) {
            throw new NotValidWishItemException();
        }
        Integer fundedAmount = contributorRepository.findFundedAmount(wishItem);
        Member member = wishItem.getMember();
        Integer itemPrice = wishItem.getItem().getPrice();
        if (itemPrice > fundedAmount) {
            throw new NoPointsException();
        }
        wishItem.getMember().addPoints(fundedAmount);
        wishItem.changeGiftStatus(GiftStatus.USED);
        return fundedAmount;
    }
}
