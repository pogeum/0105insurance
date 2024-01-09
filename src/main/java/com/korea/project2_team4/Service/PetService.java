package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.Pet;
import com.korea.project2_team4.Repository.PetRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public boolean deleteExistingFile(String existingFilePath){
        if (existingFilePath !=null && !existingFilePath.isEmpty()) {
            File existingFile = new File(existingFilePath);
            if (existingFile.exists()) {
                return existingFile.delete();
            }
        }
        return false;
    }

    public Pet getpetById(Long petid) {
        return this.petRepository.findById(petid).get();
    }

    public Pet getpetByname(String petName) {

        return this.petRepository.findByPetName(petName);
    }
    public void savePet(Pet pet) {
        this.petRepository.save(pet);
    }

    public void deletePet(Pet pet) {
        if (pet.getPetImage() !=null) {
            String filepath = pet.getPetImage().getFilePath();
            if (filepath != null && !filepath.isEmpty()) {
                deleteExistingFile(filepath);
            }
        }

        this.petRepository.delete(pet);
    }

    public void updatePet(Pet pet,String name,String content){
        Pet updatepet = this.petRepository.findById(pet.getId()).get();
        updatepet.setName(name);
        updatepet.setContent(content);
        this.petRepository.save(pet);

    }




}
