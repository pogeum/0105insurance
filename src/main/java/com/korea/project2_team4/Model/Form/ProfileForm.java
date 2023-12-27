package com.korea.project2_team4.Model.Form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileForm {
    private String profileName;
    private String content;
    private MultipartFile profileImage;
}
