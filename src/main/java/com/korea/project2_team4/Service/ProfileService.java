package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Pet;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Repository.ImageRepository;
import com.korea.project2_team4.Repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ImageService imageService;

    public boolean deleteExistingFile(String existingFilePath) {
        if (existingFilePath != null && !existingFilePath.isEmpty()) {
            File existingFile = new File(existingFilePath);
            if (existingFile.exists()) {
                return existingFile.delete();
            }
        }
        return false;
    }

    //    public void deletePet(Pet pet) {
//        String filepath = pet.getPetImage().getFilePath();
//        if (filepath != null && !filepath.isEmpty()) {
//            deleteExistingFile(filepath);
//        }
//        this.petRepository.delete(pet);
//    }


    public List<Profile> getProfilelist() {
        return this.profileRepository.findAll();
    }

    public Profile getProfileById(Long profileId) {
        return this.profileRepository.findById(profileId).get();
    }

    public void updateprofile(Profile profile, String profilename, String content) {
        profile.setProfileName(profilename);
        profile.setContent(content);
        this.profileRepository.save(profile);
    }

    public Profile setDefaultProfile(Member member) {
        Profile profile = new Profile();
        profile.setMember(member);
        profile.setProfileName(member.getUserName());
        profile.setContent(" ");

        this.profileRepository.save(profile);
        try {
            imageService.saveDefaultImgsForProfile(profile);
        } catch(Exception e) {

        }
        return this.profileRepository.save(profile);

    }

    public void setPetforprofile(Profile profile, Pet pet) {
        profile.getPetList().add(pet);
        this.profileRepository.save(profile);
    }


}
