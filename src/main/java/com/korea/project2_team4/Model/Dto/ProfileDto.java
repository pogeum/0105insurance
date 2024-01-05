package com.korea.project2_team4.Model.Dto;

import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDto {

    private Profile profile;
    private List<Post> postList;
    private List<Profile> followers;
    private List<Profile> followings;

}
