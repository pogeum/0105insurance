package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Pet;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Model.Form.ProfileForm;
import com.korea.project2_team4.Service.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import lombok.Builder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;


@Controller
@Builder
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private HttpSession session;
    private final ProfileService profileService;
    private final PostService postService;
    private final MemberService memberService;
    private final PetService petService;
    private final ImageService imageService;

    @GetMapping("/detail") // @AuthenticationPrincipal
    public String profileDetail(Model model,  Principal principal, @RequestParam(name = "postid", required = false) Long postid) {
        if (principal == null) { //postid가 null일수가 없음
            Post thispost = postService.getPost(postid);
            model.addAttribute("postList", postService.getPostsbyAuthor(thispost.getAuthor()));
            model.addAttribute("profile", thispost.getAuthor());
            return "Profile/profile_detail";
        } else { // postid는 null이거나 아예없거나 있는걸로? 예외처리로 해야하는듯
            if (postid == null) { //principal있고, postid받아온거 없을때
                Member sitemember = this.memberService.getMember(principal.getName());
                List<Post> myPosts = postService.getPostsbyAuthor(sitemember.getProfile());
                model.addAttribute("postList", myPosts);
                model.addAttribute("profile", sitemember.getProfile());
                return "Profile/profile_detail";
            } else { // 회원이 포스트에서 프로필누를때? principal있고, postid받아온거 있을때,,
                Member sitemember = this.memberService.getMember(principal.getName());
                List<Post> myPosts = postService.getPostsbyAuthor(sitemember.getProfile());
                model.addAttribute("postList", myPosts);
                model.addAttribute("profile", sitemember.getProfile());
                return "Profile/profile_detail";
            }
//            try{ // 회원이 포스트에서 프로필누를때? principal있고, postid받아온거 있을때,,
//                Member sitemember = this.memberService.getMember(principal.getName());
//                List<Post> myPosts = postService.getPostsbyAuthor(sitemember.getProfile());
//                model.addAttribute("postList", myPosts);
//                model.addAttribute("profile", sitemember.getProfile());
//                return "Profile/profile_detail";
//            } catch (IllegalArgumentException e) { //principal있고, postid받아온거 없을때
//                Member sitemember = this.memberService.getMember(principal.getName());
//                List<Post> myPosts = postService.getPostsbyAuthor(sitemember.getProfile());
//                model.addAttribute("postList", myPosts);
//                model.addAttribute("profile", sitemember.getProfile());
//                return "Profile/profile_detail";
//            }
        }

//        try {
//            if (principal == null) {
//                Post thispost = postService.getPost(postid);
//                model.addAttribute("postList", postService.getPostsbyAuthor(thispost.getAuthor()));
//                model.addAttribute("profile", thispost.getAuthor());
//                return "Profile/profile_detail";
//            } else {
//                Member sitemember = this.memberService.getMember(principal.getName());
//                List<Post> myPosts = postService.getPostsbyAuthor(sitemember.getProfile());
//                model.addAttribute("postList", myPosts);
//                model.addAttribute("profile", sitemember.getProfile());
//                return "Profile/profile_detail";
//            }
//        } catch (IllegalArgumentException e) {
//            // postid가 요청에 없거나 값이 null인 경우에 대한 예외 처리
//            // 예: 기본 페이지로 리다이렉트 또는 에러 페이지 표시
//            return "community_main";
//        }
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String profileupdate(Model model, ProfileForm profileForm,Principal principal) {
        Member sitemember = this.memberService.getMember(principal.getName());

        profileForm.setProfileName(sitemember.getProfile().getProfileName());
        profileForm.setContent(sitemember.getProfile().getContent());
        return "Profile/profile_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String profileupdate(ProfileForm profileForm, @RequestParam(value = "profileImage") MultipartFile newprofileImage,
                                BindingResult bindingResult, Principal principal)throws Exception {
        Member sitemember = this.memberService.getMember(principal.getName());

        if (profileForm.getProfileImage() !=null && !profileForm.getProfileImage().isEmpty()) {
            imageService.saveImgsForProfile(sitemember.getProfile(), newprofileImage); // 기존 이미지 먼저 지우게 된다.
        }

        profileService.updateprofile(sitemember.getProfile(),profileForm.getProfileName(),profileForm.getContent());



        return "redirect:/profile/detail";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deleteProfileImage")
    public String deleteProfileImage(@RequestParam("profileid")Long profileid) {// 일단안씀.
        Profile profile = profileService.getProfileById(profileid);
        imageService.deleteProfileImage(profile);

        return "redirect:/profile/detail";
    }


    @GetMapping("/showallPostsBy")
    public String showAllMyPosts(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam("profileid")Long profileid, Principal principal) {
        if (principal == null) {
            List<Post> myposts = postService.getPostsbyAuthor(profileService.getProfileById(profileid));
            model.addAttribute("searchResults", myposts);
            return "search_form";
        } else {
            Member sitemember = this.memberService.getMember(principal.getName());
            List<Post> myposts = postService.getPostsbyAuthor(sitemember.getProfile());

            model.addAttribute("searchResults", myposts);
            return "search_form";
//        return "community_main";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage")
    public String myPage(Model model, Principal principal) {
        Member sitemember = this.memberService.getMember(principal.getName());

        model.addAttribute("siteMember", sitemember);
        return "Member/myPage";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPage/update")
    public String memberUpdate(Principal principal,Model model){
        Member sitemember = this.memberService.getMember(principal.getName());
        model.addAttribute("siteMember",sitemember);
        return "/Member/updateMember_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/delete")
    public String memberDelete(Principal principal){
        Member member = this.memberService.getMember(principal.getName());
        memberService.delete(member);

        session.invalidate();
        return "redirect:/";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/myPage/update")
    public String memberUpdate(Principal principal,String nickName,String phoneNum,String email){
        Member sitemember = this.memberService.getMember(principal.getName());
        sitemember.setNickName(nickName);
        sitemember.setEmail(email);
        sitemember.setPhoneNum(phoneNum);
        memberService.save(sitemember);

        return "redirect:/profile/myPage";
    }
//    @GetMapping("/profile")
//    public String profile(Authentication authentication, Principal principal, UserPasswordForm userPasswordForm, Model model){
//        String userId = principal.getName();
//        User userinfo = this.userService.getUser(userId);
//        model.addAttribute("userinfo", userinfo);
//
//        return "userProfile/profile";
//    }




// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓펫 관리↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/addpet")
    public String addpet(@RequestParam("name") String name ,@RequestParam("content")String content , Principal principal ,
                         MultipartFile imageFile) throws Exception, NoSuchAlgorithmException {
        Member sitemember = this.memberService.getMember(principal.getName());
        Pet pet = new Pet();


        pet.setName(name);
        pet.setContent(content);
        pet.setOwner(sitemember.getProfile());
        petService.savePet(pet);
//
//        if (imageFile != null && !imageFile.isEmpty() && pet !=null) { //이미지 첨부를 했을때
//            imageService.saveImgsForPet(pet,imageFile); // 기존이미지잇으면 지우고 등록함.
//        }

        if (imageFile == null || imageFile.isEmpty()) {
            if (pet.getPetImage() == null ) {
                imageService.saveDefaultImgsForPet(pet);
            }
        } else if ( pet != null ){
            imageService.saveImgsForPet(pet,imageFile);
        }

        profileService.setPetforprofile(sitemember.getProfile(),pet);

        return "redirect:/profile/detail";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/deletepet")
    public String deletepet(@RequestParam("petid")Long petid) {
        Pet pet = petService.getpetById(petid);
        petService.deletePet(pet);
        return "redirect:/profile/detail";
    }




    @GetMapping("/petprofile")
    public String petprofile(Model model, @RequestParam(name="petid", required=false)Long petid) {
        Pet pet = petService.getpetById(petid);

        model.addAttribute("pet", pet);
        return "Profile/pet_profile";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updatepet")
    public String profileupdate(@RequestParam(value = "petImage") MultipartFile newpetImage, @RequestParam("petid") Long petid,
                                @RequestParam(value="name") String name, @RequestParam(value="content") String content,
                                Principal principal)throws Exception {
//        Member sitemember = this.memberService.getMember(principal.getName());
        Pet pet = petService.getpetById(petid);


        if (newpetImage !=null && !newpetImage.isEmpty()) {
            imageService.saveImgsForPet(pet, newpetImage); // 이미지처리따로.
        }

        petService.updatePet(pet,name,content);

        return "redirect:/profile/petprofile";
    }




}