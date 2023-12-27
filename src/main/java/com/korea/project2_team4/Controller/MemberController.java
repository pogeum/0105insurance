package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Config.OAuth2.OAuth2UserInfo;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Form.MemberCreateForm;
import com.korea.project2_team4.Model.Form.MemberResetForm;
import com.korea.project2_team4.Service.FollowService;
import com.korea.project2_team4.Service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.boot.Banner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.swing.*;
import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8888")
@Controller
@RequestMapping("/member")
@Builder
public class MemberController {

    private final MemberService memberService;
    private final FollowService followService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/signup")
    public String signup(MemberCreateForm memberCreateForm) {

        return "Member/signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "Member/signup_form";
        }

        if (!memberCreateForm.getPassword().equals(memberCreateForm.getRe_password())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "Member/signup_form";
        }

        try {
            memberService.create(memberCreateForm);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "Member/signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "Member/signup_form";
        }

        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/resetPassword")
    public String resetPassword(Model model, MemberResetForm memberResetForm) {
        model.addAttribute("memberResetForm", memberResetForm);
        return "Member/reset_password_from";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/resetPassword")
    public String resetPassword(@Valid MemberResetForm memberResetForm, BindingResult bindingResult, Principal principal) {
        Member member = memberService.getMember(principal.getName());
        if (bindingResult.hasErrors()) {
            return "Member/reset_password_from";
        }
        if (!passwordEncoder.matches(memberResetForm.getPassword(), member.getPassword())) {
            bindingResult.rejectValue("password", "passwordInCorrect",
                    "기존의 패스워드와 일치하지 않습니다.");
            return "Member/reset_password_from";
        }
        if (!memberResetForm.getNew_password().equals(memberResetForm.getRe_password())) {
            bindingResult.rejectValue("re_password", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "Member/reset_password_from";
        }

        memberService.resetPassword(memberResetForm, principal);

        return "redirect:/profile/myPage";
    }

    @GetMapping("/login")
    public String login() {

        return "Member/login_form";
    }

    @PostMapping("/login")
    public String login(String username, String password) {
        return "redirect:/";
    }

    @GetMapping("/signup/social")
    public String signup(MemberCreateForm memberCreateForm,
                         HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        OAuth2UserInfo socialLogin = (OAuth2UserInfo) session.getAttribute("SOCIAL_LOGIN");

        if (socialLogin != null) {
            memberCreateForm.setNickName(socialLogin.getName());
            memberCreateForm.setUserName(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setPassword(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setRe_password(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setRealName(socialLogin.getName());
            memberCreateForm.setEmail(socialLogin.getEmail());
            memberCreateForm.setProvider(socialLogin.getProvider());
            memberCreateForm.setProviderID(socialLogin.getProviderId());
//            memberCreateForm.setSnsImage(socialLogin.getImage());
        }
        model.addAttribute("socialLogin", socialLogin);
        return "Member/social_signup_form";
    }

    @PostMapping("/signup/social")
    public String signup(@Valid MemberCreateForm memberCreateForm, BindingResult bindingResult,
//                         @RequestParam(value = "profile-picture", defaultValue = "") String profile,
                         HttpServletRequest request, Model model) {

        HttpSession session = request.getSession();
        OAuth2UserInfo socialLogin = (OAuth2UserInfo) session.getAttribute("SOCIAL_LOGIN");

        if (socialLogin != null) {
            memberCreateForm.setNickName(socialLogin.getName());
            memberCreateForm.setUserName(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setPassword(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setRe_password(socialLogin.getProvider() + "_" + socialLogin.getProviderId());
            memberCreateForm.setRealName(socialLogin.getName());
            memberCreateForm.setEmail(socialLogin.getEmail());
            memberCreateForm.setProvider(socialLogin.getProvider());
            memberCreateForm.setProviderID(socialLogin.getProviderId());
//            memberCreateForm.setSnsImage(socialLogin.getImage());
        }
        model.addAttribute("socialLogin", socialLogin);

        if (bindingResult.hasErrors()) {
            return "Member/social_signup_form";
        }

        if (!memberCreateForm.getPassword().equals(memberCreateForm.getRe_password())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "Member/social_signup_form";
        }

        try {

//            소셜 로그인 프로필 이미지
//
//            if (profile.equals("sns-picture")) {
//                memberCreateForm.setImageType(0);
//            } else {
//                memberCreateForm.setImageType(1);
//            }

            Member member = memberService.create(memberCreateForm);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자 입니다.");
            return "/Member/social_signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "Member/social_signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/member")
    public String saveDefaultAdmin() {
        memberService.saveDefaultAdmin();

        return "redirect:/";

    }

    @GetMapping("/member1")
    public String saveDefaultUser() {
        memberService.saveDefaultUser();

        return "redirect:/";

    }

    @GetMapping("/findUserName")
    public String findUserName(Model model) {
        return "Member/findUserName_form";
    }

    @PostMapping("/sendVerificationCode")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String realName, @RequestParam String email,
                                                       HttpSession session) {
        try {
            if (memberService.checkMemberByEmail(email)) {
                String verificationCode = memberService.generateRandomCode();
                memberService.SendVerificationCode(email, verificationCode);

                // 클라이언트에게 전송된 인증 코드를 세션에 저장
                session.setAttribute("expectedVerificationCode", verificationCode);

                return ResponseEntity.ok("Verification code sent successfully!");
            }
            return ResponseEntity.badRequest().body("User not found for the provided email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending verification code: " + e.getMessage());
        }
    }

    @PostMapping("/findUserName")
    public String findUserName(@RequestParam String verificationCode,
                               @SessionAttribute("expectedVerificationCode") String expectedVerificationCode,
                               SessionStatus sessionStatus,
                               HttpSession session,
                               String realName, String email,Model model) {

        // 클라이언트에게 전송된 인증 코드와 서버에서 예상하는 인증 코드가 일치하는지 확인
        if (verificationCode.equals(expectedVerificationCode)) {
            // 검증에 성공하면 세션에서 인증 코드 제거
            sessionStatus.setComplete();
            Member member = memberService.foundUser(realName, email);
            model.addAttribute("foundMember",member);
            return "Member/foundUserName_form";
        }
        return "redirect:/member/findUserName_form";
    }


}
