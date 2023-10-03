package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.FriendWith;
import Team4.TobeHonest.domain.Member;

import Team4.TobeHonest.dto.FriendWithSpecifyName;

import Team4.TobeHonest.exception.DuplicateFriendException;
import Team4.TobeHonest.repo.FriendRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;


    @Transactional
    public FriendWith addFriendList(Member owner, Member friend) {
        //이미 존재하는 친구인 경우..
        if (!friendRepository.findFriend(owner, friend).isEmpty()){
            throw new DuplicateFriendException("이미 존재하는 친구입니다");
        }
        //친구 등록
        FriendWith friendWith = owner.addFriend(friend);
        //db에 저장
        friendRepository.join(friendWith);

        return friendWith;
    }

    public List<FriendWith> findAllFriends(Member member) {
        return friendRepository.findAllFriends(member);
    }

    //친구들의 모든 프로필정보를 받아오기
    public List<FriendWithSpecifyName> findAllFriendsProfile(Member member){
        return friendRepository.findAllFriendsWithSpecifyName(member);
    }




}