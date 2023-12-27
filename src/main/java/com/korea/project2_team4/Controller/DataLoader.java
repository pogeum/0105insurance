package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.Tag;
import com.korea.project2_team4.Repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final TagRepository tagRepository;

    @Autowired
    public DataLoader(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createTags();
    }

    private void createTags() {
        createTagIfNotExists("강아지");
        createTagIfNotExists("고양이");
        createTagIfNotExists("기타");
    }

    private void createTagIfNotExists(String tagName) {
        if (!tagRepository.existsByName(tagName)) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tagRepository.save(tag);
        }
    }
}
