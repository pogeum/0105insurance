package com.korea.project2_team4.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 채팅을 위한 DTO 입니다.
// 채팅 타입에 따라서 동작하는 구조가 달라진다.
// TALK 는 말 그대로 내용이 해당 채팅방을 SUB 하고 있는 모든 클라이언트에게 전달된다.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDTO {
    public enum MessageType {
        ENTER, TALK, LEAVE;
    }

    private MessageType type; //메시지 타입
    private String roomId; // 방 번호
    private String sender; // 채팅을 보낸 사람
    private String message; // 메시지
    private String time; // 채팅발송 시간

}
