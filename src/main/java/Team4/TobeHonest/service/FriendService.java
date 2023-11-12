package Team4.TobeHonest.service;


import Team4.TobeHonest.domain.FriendWith;
import Team4.TobeHonest.domain.Member;
import Team4.TobeHonest.dto.friendWIth.FriendWithSpecifyName;
import Team4.TobeHonest.exception.DuplicateFriendException;
import Team4.TobeHonest.exception.NoMemberException;
import Team4.TobeHonest.exception.NoSuchFriendException;
import Team4.TobeHonest.repo.ContributorRepository;
import Team4.TobeHonest.repo.FriendRepository;
import Team4.TobeHonest.repo.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
@Slf4j
public class FriendService {

    private final FriendRepository friendRepository;
    private final ContributorRepository contributorRepository;
    private final MemberRepository memberRepository;

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
    //오버로딩, 동작은 같음 ==> 테스트 코드 바꾸기 귀찮..
    @Transactional
    public FriendWithSpecifyName addFriendList(Member owner, Long friendId) {

        Member friend = memberRepository.findById(friendId);
        if (friend == null){
            throw new NoMemberException();
        }

        //이미 존재하는 친구인 경우..
        if (!friendRepository.findFriend(owner, friend).isEmpty()){
            throw new DuplicateFriendException("이미 존재하는 친구입니다");
        }

        //친구 등록
        FriendWith friendWith = owner.addFriend(friend);

        //db에 저장
        friendRepository.join(friendWith);
        FriendWithSpecifyName friendWithSpecifyName = this.searchFriendWithName(owner, friend.getEmail()).get(0);
        friendWithSpecifyName.setMyGive(false);
        friendWithSpecifyName.setMyTake(false);

        return friendWithSpecifyName;
    }



    public List<FriendWith> findAllFriends(Member member) {
        return friendRepository.findAllFriends(member);
    }

    //친구들의 모든 프로필정보를 받아오기
    public List<FriendWithSpecifyName> findAllFriendsProfile(Member member){

        List<FriendWithSpecifyName> result = friendRepository.findAllFriendsWithSpecifyName(member);
        //내가 준 사람들
        HashSet<Long> myContributions = new HashSet<>(contributorRepository.findMyContributions(member.getId()));
        //나에게 준사람들
        HashSet<Long> allContributors = new HashSet<>(contributorRepository.findAllContributors(member.getId()));

        for (FriendWithSpecifyName friendWithSpecifyName : result) {
            //내가 준사람인가?
            friendWithSpecifyName.setMyGive(myContributions.contains(friendWithSpecifyName.getFriendId()));
            //나에게 준 사람인가?
            friendWithSpecifyName.setMyTake(allContributors.contains(friendWithSpecifyName.getFriendId()));
        }
        return result;
    }

    public List<FriendWithSpecifyName> searchFriendWithName(Member member, String friendEmail){
        return friendRepository.searchFriendsWithEmail(member, friendEmail);
    }

    public List<Long> searchFriendWithNameOnlyFriendIdReturn(Member member, String startsWith){
        return friendRepository.searchFriendsWithNameOnlyFriendId(member, startsWith);
    }




    @Transactional
    public void deleteFriend(Member member, Long friendId){
        List<FriendWith> friend = friendRepository.findFriend(member, friendId);
        if (friend.isEmpty()){
            throw new NoSuchFriendException("해당 친구가 존재하지 않습니다.");
        }
        friendRepository.delete(friend.get(0));
    }

    public boolean isFriend(Member owner, Member friend){
        return !friendRepository.findFriend(owner, friend).isEmpty();
    }






}