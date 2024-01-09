package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Pet;
import com.korea.project2_team4.Model.Entity.TagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagMapRepository extends JpaRepository<TagMap, Long> {

    List<TagMap> findByPostId(Long id);

    List<TagMap> findByTagId(Long id);



}
