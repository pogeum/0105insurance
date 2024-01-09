package com.korea.project2_team4.Model.Form;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostForm {

    private String title;
    private String content;

    private String category;

    private List<MultipartFile> fileList; // 이미지 업로드를 위한 필드
}
