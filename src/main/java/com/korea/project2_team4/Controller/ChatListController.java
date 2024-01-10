package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.ChatRoom;
import com.korea.project2_team4.Service.ChatService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// 채팅방을 조회, 생성, 입장을 관리하는 Controller
@Controller
@Slf4j
@Builder
@RequestMapping("/chatList")
public class ChatListController {

    private final ChatService chatService;

    @GetMapping("/showList")
    public String goChatRoom(Model model) {
        model.addAttribute("list", chatService.findAllRoom());
        log.info("SHOW ALL ChatList{}", chatService.findAllRoom());

        return "Chat/chatList_form";
    }

    @PostMapping("/createRoom")
    public String createRoom(@RequestParam String name, RedirectAttributes rttr) {
        ChatRoom room = chatService.createChatRoom(name);
        log.info("CREATE Chat Room {}", room);
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chatList/showList";
    }

    @GetMapping("/room")
    public String roomDetail(Model model, String roomId) {
        log.info("roomId {}", roomId);
        model.addAttribute("room", chatService.findRoomById(roomId));
        return "Chat/chatRoom_form";
    }


}
