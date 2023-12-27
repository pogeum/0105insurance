package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.Tag;
import com.korea.project2_team4.Repository.TagRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Builder
@Service
public class TagService {
    private final TagRepository tagRepository;

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag getTagByTagName(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseThrow(() -> new RuntimeException("Tag not found for name: " + tagName));
    }
}
