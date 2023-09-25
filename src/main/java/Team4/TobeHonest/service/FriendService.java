package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.FriendWith;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.FriendProfileDTO;
import Team4.TobeHonest.dto.FriendWithSpecifyName;
import Team4.TobeHonest.exception.DuplicateFriendException;
import Team4.TobeHonest.exception.DuplicateMemberException;
import Team4.TobeHonest.repo.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;


    @Transactional
    public FriendWith addFriendList(Member owner, Member friend) {
        if (!friendRepository.findFriend(owner, friend).isEmpty()){
            throw new DuplicateFriendException("이미 존재하는 친구입니다");
        }
        FriendWith friendWith = owner.addFriend(friend);

        friendRepository.join(friendWith);
        return friendWith;
    }

    public List<FriendWith> findAllFriends(Member member) {

        return friendRepository.findAllFriends(member);
    }
    public List<FriendWithSpecifyName> findAllFriendsProfile(Member member){
        return friendRepository.findAllFriendsWithSpecifyName(member);
    }


}