package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Repository.PostRepository;
import com.korea.project2_team4.Service.*;
import lombok.Builder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


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


}

