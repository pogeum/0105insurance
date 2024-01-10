package com.korea.project2_team4.Service;


import com.korea.project2_team4.Model.Entity.TagMap;
import com.korea.project2_team4.Repository.TagMapRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Builder
@Transactional
public class TagMapService {
    private final TagMapRepository tagMapRepository;


    public void save(TagMap tagMap) {
        tagMapRepository.save(tagMap);
    }

    public void delete(TagMap tagMap) {
        tagMapRepository.delete(tagMap);
    }

    public List<TagMap> findTagMapsByPostId(Long postId) {
        return tagMapRepository.findByPostId(postId);
    }

    public List<TagMap> findTagMapsByTagId(Long tagId) {
        return tagMapRepository.findByTagId(tagId);
    }



    public void deleteTagMapsByPostId(Long postId) {
        List<TagMap> tagMaps = findTagMapsByPostId(postId);
        tagMapRepository.deleteAll(tagMaps);
    }

}
