package com.korea.project2_team4.Controller;

import com.korea.project2_team4.Model.Dto.ChatDTO;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Service.ChatService;
import com.korea.project2_team4.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

// 채팅을 수신(sub) 하고 송신(pub) 하기위한 Controller
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Slf4j
public class ChatController {
    private final SimpMessageSendingOperations template;

    private final ChatService chatService;
    private final MemberService memberService;



    @PostMapping("/createRoom")
    public String createRoom(Model model, Principal principal, @RequestParam("roomName") String roomName, @RequestParam("password") String password) {
        ChatRoom chatRoom = chatService.createChatRoom(roomName, password,principal);
        model.addAttribute("chatRoom", chatRoom);
        return "redirect:/chat/chatRoomList";
    }

    @GetMapping("/chatRoomList")
    public String showChatRoomList(Model model) {
        List<ChatRoomListResponseDto> chatRooms = chatService.findAllRoom();

        model.addAttribute("list", chatRooms);
        log.info("SHOW ALL ChatList{}", chatRooms);

        return "Chat/chatList_form";
    }

    @PostMapping("/chatRoom")
    public String goChatRoom(Model model,Principal principal, @RequestPart("roomId") Long id, @RequestPart("password") String password) {
        System.out.println(id);
        System.out.println(password);
        if (password != null) {
            chatService.enterChatRoom(id, password, principal);
        }

        ChatRoom chatRoomId = chatService.findChatRoomById(id);

        Map<String, Object> chatData = chatService.showChatDate(id);

        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("chatRoomName", chatRoomId.getRoomName());

        model.addAttribute("members", chatData.get("members"));
        model.addAttribute("messages", chatData.get("messages"));

        return "Chat/chatRoom_form";
    }

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDTO chatDTO, Principal principal) {

        try {

            chatDTO.setSender(principal.getName());

            chatService.saveChatMessage(chatDTO, principal);
            template.convertAndSend("/sub/chat/chatRoom/id/" + chatDTO.getRoomId(), chatDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
