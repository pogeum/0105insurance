package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @RequestMapping("/send-email")
    @ResponseBody
    public String sendEmail() {
        String to = "khg10271215@naver.com";
        String subject = "Test Email";
        String body = "This is a test email.";

        emailService.sendEmail(to, subject, body);

        return "Email sent successfully!";
    }
}
