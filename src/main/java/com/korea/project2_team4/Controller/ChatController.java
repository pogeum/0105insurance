//package com.korea.project2_team4.Controller;
//
//import com.korea.project2_team4.Model.Dto.ChatDTO;
//import com.korea.project2_team4.Model.Entity.Member;
//import com.korea.project2_team4.Service.ChatService;
//import com.korea.project2_team4.Service.MemberService;
//import lombok.Builder;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//import java.util.ArrayList;
//
//// 채팅을 수신(sub) 하고 송신(pub) 하기위한 Controller
//@Controller
//@Slf4j
//@Builder
//public class ChatController {
//    private final SimpMessageSendingOperations template;
//
//    private final ChatService chatService;
//
//    //MessageMapping 을 통해 WebSocket 으로 들어오는 메시지를 발신 처리한다.
//    // 이때 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
//    // 처리가 완료되면 /sub/chat/room/roomId 로 메시지가 전송된다.
//    @MessageMapping("/chat/enterUser")
//    public void enterUser(@Payload ChatDTO chat, SimpMessageHeaderAccessor headerAccessor) {
//        chatService.plusUserCnt(chat.getRoomId());
//        String userUUID = chatService.addUser(chat.getRoomId(), chat.getSender());
//
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
//
//        chat.setMessage(chat.getSender() + "님이 입장하였습니다");
//        template.convertAndSend("/sub/chat/room?roomId=" + chat.getRoomId(), chat);
//
//    }
//
//    @MessageMapping("/chat/sendMessage")
//    public void sendMessage(@Payload ChatDTO chat) {
//        log.info("Chat {}", chat);
//
//        chat.setMessage(chat.getMessage());
//
//        template.convertAndSend("/sub/chat/room?roomId=" + chat.getRoomId(), chat);
//
//    }
//
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        log.info("DisConnectEvent {}", event);
//
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
//
//        log.info("headerAccessor {}", headerAccessor);
//
//        // 채팅방 유저 -1
//        chatService.minusUserCnt(roomId);
//
//        // 채팅방 유저리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
//
//        String userName = chatService.getUserName(roomId, userUUID);
//        chatService.delUser(roomId, userUUID);
//
//        if (userName != null) {
//            log.info("User DisConnected : " + userName);
//
//            ChatDTO chat = ChatDTO.builder()
//                    .type(ChatDTO.MessageType.LEAVE)
//                    .sender(userName)
//                    .message(userName + "님이 퇴장하였습니다.")
//                    .build();
//
//            template.convertAndSend("/sub/chat/room?roomId=" + roomId, chat);
//        }
//
//    }
//
//
//
//}
