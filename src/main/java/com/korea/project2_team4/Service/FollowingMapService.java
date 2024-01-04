package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.FollowingMap;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Repository.FollowingMapRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
public class FollowingMapService {
    private final FollowingMapRepository followingMapRepository;

    public void savefollowingMap(Profile follower, Profile followee){
        if (!followingMapRepository.existsByFollowerAndFollowee(follower, followee)) {
            FollowingMap followingMap = new FollowingMap();
            followingMap.setFollower(follower);
            followingMap.setFollowee(followee);
            this.followingMapRepository.save(followingMap);
        } else {
            // 중복이면 처리할 로직 추가
            // 예: 이미 존재하는 경우 예외 처리 또는 로깅 등
            System.out.println("중복된 FollowingMap이 이미 존재합니다.");
        }

    }

    public void deletefollowingMap(Profile follower, Profile followee){
        if (followingMapRepository.existsByFollowerAndFollowee(follower, followee)) {
            FollowingMap unfollowingMap = this.followingMapRepository.findByProfiles(follower.getId(), followee.getId());
            this.followingMapRepository.delete(unfollowingMap);
        } else {
            // 처리할 로직 추가
            System.out.println("존재하지 않는 FollowingMap입니다.");
        }

    }

    public List<Profile> getMyfollowers(Profile profile){
        List<FollowingMap> mapList = this.followingMapRepository.findAllByMyFollowers(profile.getId()); //내팔로워찾는거 팔로이 에서 찾아야댐
        List<Profile> followerList = new ArrayList<>();
        for (FollowingMap followingMap : mapList) {
            Profile follower = followingMap.getFollower();
            followerList.add(follower);
        }
        return followerList;
    }

    public List<Profile> getMyfollowings(Profile profile) { //내가팔로우하는사람 찾는거 팔로우에서 찾아야댐
        List<FollowingMap> mapList = this.followingMapRepository.findAllFollowings(profile.getId());
        List<Profile> followingList = new ArrayList<>();
        for (FollowingMap followingMap : mapList) {
            Profile following = followingMap.getFollowee();
            followingList.add(following);
        }
        return followingList;
    }
}
