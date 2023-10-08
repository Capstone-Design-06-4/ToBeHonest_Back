package Team4.TobeHonest.controller;

import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.contributor.ContributorDTO;
import Team4.TobeHonest.dto.item.ItemInfoDTO;
import Team4.TobeHonest.dto.wishitem.FirstWishItem;
import Team4.TobeHonest.dto.wishitem.WishItemDetail;
import Team4.TobeHonest.dto.wishitem.WishItemResponseDTO;
import Team4.TobeHonest.exception.DuplicateWishItemException;
import Team4.TobeHonest.service.ContributorService;
import Team4.TobeHonest.service.ItemService;
import Team4.TobeHonest.service.WishItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class WishlistController {

    private final WishItemService wishItemService;
    private final ContributorService contributorService;
    private final ItemService itemService;

    //    위시리스트 정보들(사진, progress정도.. 추가 정보필요하면 FrirstWishItem수정하기)
    @GetMapping("{memberId}")
    public List<FirstWishItem> inquireWishList(@PathVariable Long memberId) {
        return wishItemService.findWishList(memberId);
    }


    @GetMapping("{memberId}/{wishItemId}")
    public WishItemResponseDTO seeWishItemDetail(@PathVariable Long memberId, @PathVariable Long wishItemId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {

        WishItemResponseDTO response = new WishItemResponseDTO();
        List<WishItemDetail> wishItemDetail = wishItemService.findWishItemDetail(wishItemId);
        response.setWishItemDetail(wishItemDetail);
        Member member = (Member) userDetails;
//        만약 로그인한 멤버라면... 추가정보도 제공
        List<ContributorDTO> contributor;

        if (member.getId().equals(memberId)) {
            contributor = contributorService.findContributor(wishItemId);
            response.setContributor(contributor);
        }
        return response;
    }

    @GetMapping("/add/{itemId}")
    public ResponseEntity<String> addWishItem(@PathVariable Long itemId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        ItemInfoDTO byItembyID = itemService.findByItembyID(itemId);
        try {
            wishItemService.addWishList((Member) userDetails,
                    byItembyID);
        } catch (DuplicateWishItemException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 wishItem입니다");
        }
        return ResponseEntity.status(HttpStatus.OK).body("위시아이템" + byItembyID.getName() + " 추가완료");
    }

    @GetMapping("/delete/{itemId}")
    public ResponseEntity<String> deleteWishItem(@PathVariable Long itemId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        ItemInfoDTO itemInfoDTO = itemService.findByItembyID(itemId);
        try {
            wishItemService.deleteWishListByItemId((Member) userDetails, itemInfoDTO.getId());
        } catch (DuplicateWishItemException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 wishItem입니다");
        }
        return ResponseEntity.status(HttpStatus.OK).body("위시아이템" + itemInfoDTO.getName() + " 추가완료");
    }


}
