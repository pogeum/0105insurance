package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.FollowingMap;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowingMapRepository extends JpaRepository<FollowingMap, Long> {

    @Query("SELECT f FROM FollowingMap f WHERE f.followee.id = :followeeId")
    List<FollowingMap> findAllByMyFollowers(@Param("followeeId") Long followeeId);

    @Query("SELECT f FROM FollowingMap f WHERE f.follower.id = :followerId")
    List<FollowingMap> findAllFollowings(@Param("followerId") Long followerId);


    @Query("SELECT f FROM FollowingMap f WHERE f.follower.id = :followerId AND f.followee.id = :followeeId")
    FollowingMap findByProfiles(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    boolean existsByFollowerAndFollowee(Profile follower, Profile followee);


}
