package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Tag;
import com.korea.project2_team4.Repository.TagRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return tagRepository.findByName(tagName).get();
//                .orElseThrow(() -> new RuntimeException("Tag not found for name: " + tagName));
    }

    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    public void deleteById(Long id) {
        tagRepository.deleteById(id);
    }

    public boolean tagExists(String tagName) {
        // 태그 이름을 사용하여 데이터베이스에서 태그를 조회하고 존재 여부를 반환
        return tagRepository.existsByName(tagName);

    }
    public List<Tag> getDefaultTags() {
        List<Tag> defaultTagList = new ArrayList<>();

        defaultTagList.add(getTagByTagName("강아지"));
        defaultTagList.add(getTagByTagName("고양이"));
        defaultTagList.add(getTagByTagName("기타"));

        return defaultTagList;
    }


    public List<Tag> getTagListByPost(Post post) {
        List<Tag> getTagList = new ArrayList<>();
        getTagList.addAll(post.getTagList());

        if (!containsTagWithName(getTagList, "강아지")) {
            getTagList.add(getTagByTagName("강아지"));
        }
        if (!containsTagWithName(getTagList, "고양이")) {
            getTagList.add(getTagByTagName("고양이"));
        }
        if (!containsTagWithName(getTagList, "기타")) {
            getTagList.add(getTagByTagName("기타"));
        }
        return getTagList;
    }

    public boolean containsTagWithName(List<Tag> tagList, String tagName) {
        for (Tag tag : tagList) {
            if (tag.getName().equals(tagName)) {
                return true;
            }
        }
        return false;
    }
}

