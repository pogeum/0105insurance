package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    void deleteBySaveName(String saveName);
}
