package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Repository.MemberRepository;
import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Service
public class PostService {
    private final PostRepository postRepository;
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    public void save(Post post) {
        postRepository.save(post);
    }

    public Page<Post> postList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findAll(pageable);
    }

    public Page<Post> getPostsByTagName(int page, String searchTagName) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByTagName(searchTagName, pageable);
    }

//    public Page<Post> getPostsByTagName(int page, String searchTagName, String sort, String category) {
//        Page<Post> sortedList;
//        if ( sort.equals("latest")) {
//            sortedList = this.postList(page);
//        }
//        if (sort.equals("likeCount")) {
//            sortedList = this.getPostsOrderByLikeCount(page);
//        }
//        List<Sort.Order> sorts = new ArrayList<>();
//        sorts.add(Sort.Order.desc("createDate"));
//        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
//        return postRepository.findByTagName(searchTagName, pageable);
//    } 선영추가 , 수정중

    public Page<Post> getPostsOrderByLikeCount(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findAllOrderByLikeMembersSizeDesc(pageable);
    }

    public Page<Post> getPostsOrderByCommentCount(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository.findAllOrderByCommentsSizeDesc(pageable);
    }
    public Page<Post> getMyPosts(int page,Profile author){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByAuthor(author,pageable);
    }
    public Page<Post> getMyLikedPosts(int page,Member member){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByLikeMembers(member,pageable);

    }

    //테스트 데이터
    @PostConstruct
    public void init() {
        saveTestPost();
    }

    //테스트 데이터
    @Transactional
    public void saveTestPost() {
        if (postRepository.findAll().isEmpty()) {

            Member admin = memberRepository.findByUserName("admin").orElse(null);

            Profile authorProfile = profileRepository.findByProfileName("관리자")
                    .orElseGet(() -> {
                        Profile newProfile = new Profile();
                        newProfile.setProfileName("관리자");
                        return newProfile;
                    });

            // Profile 저장 (만약 새로 생성한 경우에만 저장)
            if (!profileRepository.existsById(authorProfile.getId())) {
                profileRepository.save(authorProfile);
            }

            for (int i = 1; i <= 10; i++) {
                Post post = new Post();
                post.setTitle(String.format("테스트 데이터 제목 입니다:[%03d].", i));
                post.setContent("테스트 데이터 내용 입니다.");
                post.setCreateDate(LocalDateTime.now());

                post.setAuthor(admin.getProfile());

                postRepository.save(post);
            }
        }
    }


    // 게시물 검색기능
    public List<Post> searchPosts(String kw) {
        return postRepository.findAllByKw(kw);
    }

    public List<Post> searchPostTitle(String kw) {
        return postRepository.findByPostTitle(kw);
    }

    public List<Post> searchPostContent(String kw) {
        return postRepository.findByPostContent(kw);
    }

    public List<Post> searchProfileName(String kw) {
        return postRepository.findByProfileName(kw);
    }

    public List<Post> searchCommentContent(String kw) {
        return postRepository.findByCommentContent(kw);
    }

    // post를 optional타입으로 가져오기
    public Post getPost(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        return postOptional.orElse(null);

    }

    public Post getPostIncrementView(Long id) {
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isPresent()) {
            Post postView = postOptional.get();
            postView.setView(postView.getView() + 1);
            return postRepository.save(postView);
        }
        return postOptional.orElse(null);
    }

    //좋아요 기능
    public void Like(Post post, Member member) {
        post.getLikeMembers().add(member);
        this.postRepository.save(post);
    }

    public void unLike(Post post, Member member) {
        post.getLikeMembers().remove(member);
        this.postRepository.save(post);
    }

    public boolean isLiked(Post post, Member member) {
        if (post == null) {
            return false;
        }
        return post.getLikeMembers().contains(member);
    }

    public void deleteById(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            List<Image> postImages = post.getPostImages();

            if (postImages != null) {
                for (Image image : postImages) {
                    String filepath = image.getFilePath();

                    if (filepath != null && !filepath.isEmpty()) {
                        imageService.deleteExistingFile(filepath);
                    }
                }
            }

            this.postRepository.deleteById(id);
        }
    }

//   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 선영 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    //작성자게시글불러오기
    public List<Post> getPostsbyAuthor(Profile profile) {
        List<Post> targetPosts = this.postRepository.findAllByauthor(profile.getProfileName());
        return targetPosts;
    }
    //작성자 게시글 -> 페이징처리
    public Page<Post> myPostListPage(int page, Profile profile) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
        return postRepository.findAllByauthorPage(profile.getProfileName(), pageable);
    }

    public Page<Post> getPostsByCategory(int page, String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return postRepository.findByTagName(category, pageable);
    }





//   ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 선영 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    public Page<Post> pagingByTitle(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByPostTitleFromPaging(kw, pageable);
    }

    public Page<Post> pagingByContent(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByPostContentFromPaging(kw, pageable);
    }

    public Page<Post> pagingByProfileName(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByProfileNameFromPaging(kw, pageable);
    }

    public Page<Post> pagingByComment(String kw, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByCommentFromPaging(kw, pageable);
    }

}
