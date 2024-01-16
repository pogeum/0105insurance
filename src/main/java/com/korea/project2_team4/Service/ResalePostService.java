package com.korea.project2_team4.Service;

import com.korea.project2_team4.Repository.MemberRepository;
import com.korea.project2_team4.Repository.ProfileRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

@Builder
@Service
public class ResalePostService {

    private final MemberService memberService;
    private final ProfileService profileService;
    private final ImageService imageService;


}
