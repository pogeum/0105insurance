package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Model.Form.PostForm;
import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Service.*;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@Builder
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ProfileService profileService;
    private final CommentService commentService;
    private final ImageService imageService;
    private final MemberService memberService;
    private final TagService tagService;
    private final TagMapService tagMapService;
    private final RecentSearchService recentSearchService;


    @GetMapping("/main")
    public String main() {


        return "community_main";
    }

    @GetMapping("/createPost")
    public String createPost(Model model, PostForm postForm) {
        List<Tag> allTags = tagService.getAllTags();
        model.addAttribute("allTags", allTags);
        return "createPost_form";
    }

    //테스트 데이터
    @GetMapping("/TestPost")
    public String saveTestPost() {
        postService.saveTestPost();

        return "redirect:/";

    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/createPost")
    public String createPost(Principal principal, PostForm postForm, BindingResult bindingResult
            , @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                             @RequestParam(value = "selectedTagNames", required = false) List<String> selectedTagNames,
                             @RequestParam(value = "newTagNames", required = false) List<String> newTagNames) throws IOException, NoSuchAlgorithmException {
        //      Profile testProfile = profileService.getProfilelist().get(0);
//      profileService.updateprofile(testProfile,profileForm.getProfileName(),profileForm.getContent());
        Post post = new Post();
//        System.out.println(imageFiles.size());
        Member sitemember = this.memberService.getMember(principal.getName());
        post.setTitle(postForm.getTitle());
        post.setContent(postForm.getContent());
        post.setCreateDate(LocalDateTime.now());
        post.setAuthor(sitemember.getProfile());
        post.setCategory(postForm.getCategory());
        if (imageFiles != null && !imageFiles.isEmpty()) {
            imageService.uploadPostImage(imageFiles, post);
        }
        if (newTagNames != null && !newTagNames.isEmpty()) {
            for (String newTagName : newTagNames) {
                Tag tag = new Tag();
                tag.setName(newTagName);
                tagService.save(tag);
            }
        }
            postService.save(post);

        if (selectedTagNames != null && !selectedTagNames.isEmpty()) {
            for (String selectedTagName : selectedTagNames) {
                Tag tag = tagService.getTagByTagName(selectedTagName);
                TagMap tagMap = new TagMap();
                tagMap.setPost(post);
                tagMap.setTag(tag);
                tagMapService.save(tagMap);
//                if (newTagNames != null && !newTagNames.isEmpty()) {
//                    for (String newTagName : newTagNames) {
//                        tagService.deleteById(tagService.getTagByTagName(newTagName).getId());
//                    }
//                }
            }
        }


        return "redirect:/post/community/main";
    }

    @GetMapping("/community/main")
    public String communityMain(Principal principal, Model model, @RequestParam(name = "category", required = false) String category,
                                @RequestParam(name = "sort", required = false) String sort,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(name = "searchTagName", required = false) String searchTagName) {
        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
        Page<Post> allPosts;
        allPosts = postService.postList(page);//최신순문제잇음


        if (searchTagName == null) {
            searchTagName = "";  // 기본적으로 빈 문자열로 설정
        }
        if (category == null) {
            category = "";
        }
        if (sort == null) {
            sort = "latest";
        }

        if (searchTagName.equals("전체")) {
            if (sort.equals("likeCount")) {
                allPosts = postService.getPostsOrderByLikeCount(page);
            } else if (sort.equals("commentCount")) {
                allPosts = postService.getPostsOrderByCommentCount(page);
            } else {
                allPosts = postService.postList(page);
            }
        } else {
            allPosts = postService.getPostsBytagAndcategoryAndsort(page, searchTagName, category, sort);
        }

        //sorting 이랑 태그는 ㅇㅋ 근데 카테고리 이상 --> null들어가는거쩔수없음 . 카테고리 필수선택으로 할지?

        model.addAttribute("category", category);
        model.addAttribute("searchTagName", searchTagName);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", allPosts);
        return "community_main";
    }


    @GetMapping("/search")
    public String searchPosts(@RequestParam(value = "kw", defaultValue = "") String kw, Model model) {

        List<Post> searchResultsByPostTitle = postService.searchPostTitle(kw);
        List<Post> searchResultsByPostContent = postService.searchPostContent(kw);
        List<Post> searchResultsByProfileName = postService.searchProfileName(kw);
        List<Post> searchResultsByCommentContent = postService.searchCommentContent(kw);
        List<String> recentSearchKeywords = recentSearchService.getRecentSearchKeywords();

        Collections.reverse(searchResultsByPostTitle);
        Collections.reverse(searchResultsByPostContent);
        Collections.reverse(searchResultsByProfileName);
        Collections.reverse(searchResultsByCommentContent);

        searchResultsByPostTitle = searchResultsByPostTitle.subList(0, Math.min(5, searchResultsByPostTitle.size()));
        searchResultsByPostContent = searchResultsByPostContent.subList(0, Math.min(5, searchResultsByPostContent.size()));
        searchResultsByProfileName = searchResultsByProfileName.subList(0, Math.min(5, searchResultsByProfileName.size()));
        searchResultsByCommentContent = searchResultsByCommentContent.subList(0, Math.min(5, searchResultsByCommentContent.size()));

        model.addAttribute("searchResultsByPostTitle", searchResultsByPostTitle);
        model.addAttribute("searchResultsByPostContent", searchResultsByPostContent);
        model.addAttribute("searchResultsByProfileName", searchResultsByProfileName);
        model.addAttribute("searchResultsByCommentContent", searchResultsByCommentContent);
        model.addAttribute("recentSearchKeywords", recentSearchKeywords);
        model.addAttribute("kw", kw);

        recentSearchService.saveRecentSearch(kw);

        return "search_form";
    }

    @GetMapping("/showMoreTitle")
    public String showMoreTitle(@RequestParam(value = "kw", required = false) String kw,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {
        Page<Post> pagingByTitle = postService.pagingByTitle(kw, page);

        model.addAttribute("pagingByTitle", pagingByTitle);
        model.addAttribute("kw", kw);

        return "showMoreTitle_form";
    }

    @GetMapping("/showMoreContent")
    public String showMoreContents(@RequestParam(value = "kw", defaultValue = "") String kw,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   Model model) {
        Page<Post> pagingByContent = postService.pagingByContent(kw, page);

        model.addAttribute("pagingByContent", pagingByContent);
        model.addAttribute("kw", kw);

        return "showMoreContent_form";
    }

    @GetMapping("/showMoreProfileName")
    public String showMoreProfileNames(@RequestParam(value = "kw", defaultValue = "") String kw,
                                       @RequestParam(value = "page", defaultValue = "0") int page,
                                       Model model) {
        Page<Post> pagingByProfileName = postService.pagingByProfileName(kw, page);

        model.addAttribute("pagingByProfileName", pagingByProfileName);
        model.addAttribute("kw", kw);

        return "showMoreProfileName_form";
    }

    @GetMapping("/showMoreComment")
    public String showMoreComments(@RequestParam(value = "kw", defaultValue = "") String kw,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   Model model) {
        Page<Post> pagingByComment = postService.pagingByComment(kw, page);

        model.addAttribute("pagingByComment", pagingByComment);
        model.addAttribute("kw", kw);

        return "showMoreComment_form";
    }

    @GetMapping("/detail/{id}/{hit}")
    public String postDetail(Principal principal, Model model, @PathVariable("id") Long id, @PathVariable("hit") Integer hit, PostForm postForm) {
        if (principal != null) {
            Member member = this.memberService.getMember(principal.getName());
            model.addAttribute("loginedMember", member);
        }
        if (hit == 0) {
            Post post = postService.getPostIncrementView(id);
            model.addAttribute("post", post);
        } else {
            Post post = postService.getPost(id);
            model.addAttribute("post", post);

        }
        List<Tag> allTags = tagService.getAllTags();
        model.addAttribute("allTags", allTags);
        model.addAttribute("postForm", postForm);

        return "postDetail_form";
    }

    @PostMapping("/postLike")
    public String postLike(Principal principal, @RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            Post post = this.postService.getPost(id);
            Member member = this.memberService.getMember(principal.getName());
            Long postId = post.getId();
            boolean isChecked = false;
            if (postService.isLiked(post, member)) {
                postService.unLike(post, member);
            } else {
                postService.Like(post, member);
                isChecked = true;
            }
            redirectAttributes.addFlashAttribute("isChecked", isChecked);
            return "redirect:/post/detail/" + postId + "/1";
        } else {
            return "redirect:/member/login";
        }
    }

    @PostMapping("/deletePost/{id}")
    public String deletePost(@PathVariable Long id) {

        postService.deleteById(id);

        return "redirect:/post/community/main";
    }

    @PostMapping("/updatePost/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post updatePost, @RequestParam(value = "selectedTagNames", required = false) List<String> selectedTagNames
    ) {

        Post existingPost = postRepository.findById(id).orElse(null);

        if (existingPost != null) {

            existingPost.setTitle(updatePost.getTitle());
            existingPost.setContent(updatePost.getContent());
            existingPost.setModifyDate(LocalDateTime.now());
            tagMapService.deleteTagMapsByPostId(id);
            if (selectedTagNames != null && !selectedTagNames.isEmpty()) {
                for (String selectedTagName : selectedTagNames) {
                    Tag tag = tagService.getTagByTagName(selectedTagName);
                    TagMap tagMap = new TagMap();
                    tagMap.setPost(existingPost);
                    tagMap.setTag(tag);
                    tagMapService.save(tagMap);
                }
            }
            existingPost.setCategory(updatePost.getCategory());

            postRepository.save(existingPost);
        }

        return "redirect:/post/detail/{id}/1";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myPosts")
    public String getMyPosts(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        Profile author = memberService.getMember(principal.getName()).getProfile();
        Page<Post> myPosts = postService.getMyPosts(page, author);
        model.addAttribute("paging", myPosts);
        return "Member/findMyPosts_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myLikedPosts")
    public String getMyLikedPosts(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        Member member = memberService.getMember(principal.getName());
        Page<Post> myLikedPosts = postService.getMyLikedPosts(page, member);
        model.addAttribute("paging", myLikedPosts);
        return "Member/findMyLikedPosts_form";
    }

    //   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 선영 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓


    //   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 선영 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}







