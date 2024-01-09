package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Service.ChatService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
@RestController
@Slf4j
@Builder
@RequestMapping("/chat")
public class chatRoomController {

    private final ChatService chatService;

    // 채팅에 참가한 유저리스트 반환
    @GetMapping("/userList")
    @ResponseBody
    public ArrayList<String> userList(String roomId) {

        return chatService.getUserList(roomId);
    }

    // 채팅에 참여한 유저 닉네임 중복 확인
    @GetMapping("/duplicateName")
    @ResponseBody
    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("userName") String userName) {

        String duplicateUserName = chatService.isDuplicateUserName(roomId, userName);
        log.info("동작확인 {}", duplicateUserName);

        return duplicateUserName;
    }

}
