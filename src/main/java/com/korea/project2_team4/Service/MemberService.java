package com.korea.project2_team4.Service;

import com.korea.project2_team4.Config.UserRole;
import com.korea.project2_team4.Model.Entity.Image;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Profile;
import com.korea.project2_team4.Model.Form.EditPasswordForm;
import com.korea.project2_team4.Model.Form.MemberCreateForm;
import com.korea.project2_team4.Model.Form.MemberResetForm;
import com.korea.project2_team4.Repository.MemberRepository;
import com.korea.project2_team4.Repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Builder
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ProfileService profileService;
    private final ImageService imageService;


    // 멤버 생성
    public Member create(MemberCreateForm memberCreateForm) {
        Member member = new Member();
        member.setUserName(memberCreateForm.getUserName());
        member.setPassword(passwordEncoder.encode(memberCreateForm.getPassword()));

        member.setEmail(memberCreateForm.getEmail());
        member.setCreateDate(LocalDateTime.now());
        member.setRole(UserRole.USER.getValue());

        member.setRealName(memberCreateForm.getRealName());
        member.setNickName(memberCreateForm.getNickName());
        member.setPhoneNum(memberCreateForm.getPhoneNum());

        memberRepository.save(member);
        member.setProfile(profileService.setDefaultProfile(member));
        return member;
    }

    public void resetPassword(MemberResetForm memberResetForm, Principal principal) {
        Member member = getMember(principal.getName());

        member.setPassword(passwordEncoder.encode(memberResetForm.getNew_password()));
        memberRepository.save(member);
    }
    public void editPassword(EditPasswordForm editPasswordForm, Member member) {
        member.setPassword(passwordEncoder.encode(editPasswordForm.getNew_password()));
        memberRepository.save(member);
    }

    public Member getMember(String username) {
        return this.memberRepository.findByUserName(username).orElse(null);
    }
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버의 아이디를 찾을 수 없습니다: " + memberId));
    }

    @PostConstruct
    public void init() throws Exception {
        saveDefaultAdmin();
        saveDefaultUser();
    }

    @Transactional
    public void saveDefaultAdmin() throws Exception { // 테스트 관리자 아이디 아이디: admin, 비밀 번호 admin123!
        if (memberRepository.findByUserName("admin").isEmpty()) {
            Member member = new Member();
            member.setUserName("admin");
            member.setPassword(passwordEncoder.encode("admin123!"));
            member.setEmail("admin@gmail.com");
            member.setPhoneNum("000-0000-0000");
            member.setRole("ROLE_ADMIN");
            member.setRealName("관리자");
            member.setNickName("관리자");
            member.setCreateDate(LocalDateTime.now());
            memberRepository.save(member);


            Profile newProfile = new Profile();
            newProfile.setProfileName("관리자프로필");
            newProfile.setMember(member);
            profileRepository.save(newProfile);


//            member.setProfile(newProfile);
//            memberRepository.findById(member.getId()).get().setProfile(newProfile);

            Member adminprofile = memberRepository.findById(member.getId()).get();
            adminprofile.setProfile(newProfile);
            memberRepository.save(adminprofile);
            profileRepository.save(newProfile);



            Profile saveImage = profileRepository.findById(newProfile.getId()).get();
            imageService.saveDefaultImgsForProfile(saveImage);
            profileRepository.save(saveImage);

//            imageService.saveDefaultImgsForProfile(profileRepository.findById(newProfile.getId()).get());





        }
    }

    @Transactional
    public void saveDefaultUser() { // 테스트 유저 아이디 아이디: test1, 비밀 번호 test123!
        for (int i = 1; i <= 5; i++) {
            String userName = "test" + i;
            if (memberRepository.findByUserName(userName).isEmpty()) {
                Member member = new Member();
                member.setUserName(userName);
                member.setPassword(passwordEncoder.encode("test123!"));
                member.setEmail(userName + "@gmail.com");
                member.setPhoneNum("000-0000-0000");
                member.setRole("ROLE_USER");
                member.setRealName("테스트 유저" + i);
                member.setNickName("테스트 유저" + i);
                member.setCreateDate(LocalDateTime.now());

                memberRepository.save(member);

                final int finalI = i;

                Profile adminProfile = profileRepository.findByProfileName("테스트 유저" + finalI)
                        .orElseGet(() -> {
                            Profile newProfile = new Profile();
                            newProfile.setProfileName("테스트 유저" + finalI);
                            return newProfile;
                        });

                // Set the member for the profile
                adminProfile.setMember(member);

                profileRepository.save(adminProfile);


            }
        }
    }

    public void delete(Member member) {
        memberRepository.delete(member);
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

//    public Member foundUserByPhoneNum(String realName, String phoneNum) {
//        return memberRepository.findByRealNameAndPhoneNum(realName, phoneNum)
//                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. 실명: " + realName + ", 전화번호: " + phoneNum));
//    }
    public Member foundUser(String realName, String email) {
        return memberRepository.findByRealNameAndEmail(realName, email)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. 실명: " + realName + ", 이메일: " + email));
    }
    public Member foundUserByUserName(String realName, String email,String userName) {
        return memberRepository.findByRealNameAndEmailAndUserName(realName, email, userName)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다. 실명: " + realName + ", 이메일: " + email));
    }


    public boolean checkMemberToFindUserName(String email,String realName) {
        return memberRepository.existsByEmailAndRealName(email,realName);
    }
    public boolean checkMemberToFindPassword(String userName, String email, String realName){
        return memberRepository.existsByUserNameAndEmailAndRealName(userName,email,realName);
    }

    public void SendVerificationCode(String email,String verificationCode) {
        emailService.sendEmail(email,"PetPlanet 인증 번호 입니다","인증 번호 : "+verificationCode);
    }
    public static String generateRandomCode() {
        // 100000부터 999999까지의 랜덤 숫자 생성 (6자리)
        int randomCode = new Random().nextInt(900000) + 100000;
        return String.valueOf(randomCode);
    }
    public Page<Member> getMemberListByPage(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
        return this.memberRepository.findAll(pageable);
    }
    @jakarta.transaction.Transactional
    public void changeMemberRole(Long id, String Role) {
        Member member = memberRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("member not found"));

        member.setRole(Role);

        memberRepository.save(member);

    }
}
