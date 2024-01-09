package com.korea.project2_team4.Model.Dto;

import com.korea.project2_team4.Service.ChatService;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// 채팅룸을 위한 DTO 입니다.
// Stomp 를 통해 pub/sub 를 사용하면  구독자가 알아서 관리 된다.
// 따라서 따로 세션 관리를 하는 코드를 작성할 필요가 없다.
// 또한, 메시지를 다른 세션의 클라이언트에게 발송하는 것도 구현할 필요가 없다.

@Data
public class ChatRoom {
    private String roomId; //채팅방 아이디
    private String roomName; // 채팅방 이름
    private long userCount; // 채팅방 인원 수

    private HashMap<String, String> userList = new HashMap<String, String>();

    public ChatRoom create(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;

        return chatRoom;
    }

}
