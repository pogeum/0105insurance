package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Model.Form.PostForm;
import com.korea.project2_team4.Model.Form.ResalePostForm;
import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Service.*;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


@Controller
@Builder
@RequestMapping("/resalePost")
public class ResalePostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ProfileService profileService;
    private final CommentService commentService;
    private final ImageService imageService;
    private final MemberService memberService;
    private final TagService tagService;
    private final TagMapService tagMapService;
    private final RecentSearchService recentSearchService;
    private final ReportService reportService;
    private final ResalePostService resalePostService;

    @GetMapping("/main")
    public String resalePost(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<ResalePost> resalePostList = resalePostService.resalePostList(page);
        model.addAttribute("paging",resalePostList);
        return "resale_main";
    }

    @GetMapping("/detail/{id}/{hit}")
    public String postDetail(Principal principal, Model model, @PathVariable("id") Long id, @PathVariable("hit") Integer hit) {
        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
        if (hit == 0) {
            ResalePost resalePost = resalePostService.getResalePostIncrementView(id);
            model.addAttribute("post", resalePost);
        } else {
            ResalePost resalePost = resalePostService.getResalePost(id);
            model.addAttribute("post", resalePost);
        }
        return "ResalePost/resalePostDetail_form";
    }
    @GetMapping("/createResalePost")
    public String createPost(Model model, ResalePostForm resalePostForm) {
        model.addAttribute("resalePostForm",resalePostForm);
        return "ResalePost/createResalePost_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/createResalePost")
    public String createResalePost(Principal principal, ResalePostForm resalePostForm, BindingResult bindingResult
            , @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) throws IOException, NoSuchAlgorithmException {
        ResalePost resalePost = new ResalePost();

        Member sitemember = this.memberService.getMember(principal.getName());
        resalePost.setTitle(resalePostForm.getTitle());
        resalePost.setPrice(resalePostForm.getPrice());
        resalePost.setContent(resalePostForm.getContent());
        resalePost.setCategory(resalePostForm.getCategory());
        resalePost.setCreateDate(LocalDateTime.now());
        resalePost.setSeller(sitemember.getProfile());
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageService.uploadResalePostImage(imageFiles, resalePost);
        }

        resalePostService.save(resalePost);


//        String encodedCategory;
//        try {
//            encodedCategory = URLEncoder.encode(postForm.getCategory(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            // 예외 처리 필요
//            encodedCategory = "";
//        }

        return "redirect:/resalePost/detail/"+ resalePost.getId() +"/0";
    }
    @PostMapping("/deleteResalePost/{id}")
    public String deletePost(@PathVariable Long id) {

        resalePostService.deleteById(id);

        return "redirect:/resalePost/main";
    }
    @PostMapping("/resalePostWish")
    public String resalePostWish(Principal principal, @RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            ResalePost resalePost = this.resalePostService.getResalePost(id);
            Member member = memberService.getMember(principal.getName());
            Profile profile = member.getProfile();
            //프로필 이름은 멤버의 닉네임 이다. principal.getName() 은 username이다. principal.getName()으로 profileService.getProfileByName 메서드를 사용할 수 없음.
            Long postId = resalePost.getId();
            boolean isChecked = false;
            if (resalePostService.getWished(resalePost, profile)) {
                resalePostService.cancelWish(resalePost, profile);
            } else {
                resalePostService.wish(resalePost, profile);
                isChecked = true;
            }
            redirectAttributes.addFlashAttribute("isChecked", isChecked);
            return "redirect:/resalePost/detail/" + postId + "/1";
        } else {
            return "redirect:/member/login";
        }
    }
}

