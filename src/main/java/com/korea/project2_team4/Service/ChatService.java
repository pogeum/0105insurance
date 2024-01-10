package com.korea.project2_team4.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korea.project2_team4.Model.Dto.ChatDTO;
//import com.korea.project2_team4.Model.Dto.ChatRoom;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatMessage;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Model.Entity.MemberChatRoom;
import com.korea.project2_team4.Repository.ChatRepository;
import com.korea.project2_team4.Repository.ChatRoomRepository;
import com.korea.project2_team4.Repository.MemberChatRoomRepository;
import com.korea.project2_team4.Repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Data
@Service
public class ChatService {
    private Map<String, ChatRoom> chatRoomMap;
    private final MemberService memberService;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    // 전체 채팅방 조회
    // 채팅방 생성 순서를 최근 순으로 반환
    public List<ChatRoomListResponseDto> findAllRoom() {
        List<ChatRoomListResponseDto> chatRooms = new ArrayList<>();

        chatRoomRepository.findAll().forEach(chatRoom -> {
            chatRooms.add(ChatRoomListResponseDto.builder()
                    .roomName(chatRoom.getRoomName())
                    .build());
        });
        Collections.reverse(chatRooms);
        return chatRooms;
    }


    // roomName 으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName, String userName) {
        //채팅룸 이름으로 채팅방을 먼저 생성한다

        Member member = memberRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("Member not found"));
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .build();

        chatRoomRepository.save(chatRoom);

        // create 에서는 admin 권한을 부여
        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .admin(true)
                .chatroom(chatRoom)
                .member(member)
                .build();

        memberChatRoomRepository.save(memberChatRoom);

        return chatRoom;
    }

    public void saveChatMessage(ChatDTO chatDTO) {
        //ChatDTO 에서 필요한 정보를 추출하여 ChatMessage 엔티티에 저장

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(chatDTO.getMessage());

        Optional<Member> optionalSender = memberRepository.findByUserName(chatDTO.getSender());
        Member sender = optionalSender.orElseThrow(() -> new RuntimeException("Member not found"));
        chatMessage.setSender(sender);


        chatMessage.setTime(LocalDateTime.parse(chatDTO.getTime()));

        chatRepository.save(chatMessage);
    }

}