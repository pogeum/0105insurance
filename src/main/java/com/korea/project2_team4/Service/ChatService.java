package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Dto.ChatDTO;
//import com.korea.project2_team4.Model.Dto.ChatRoom;
import com.korea.project2_team4.Model.Dto.ChatRoomListResponseDto;
import com.korea.project2_team4.Model.Entity.ChatMessage;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.ChatRoom;
import com.korea.project2_team4.Model.Entity.MemberChatRoom;
import com.korea.project2_team4.Repository.*;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ChatMessageRepository chatMessageRepository;

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
                    .id(chatRoom.getId())
                    .roomName(chatRoom.getRoomName())
                    .adminName(chatRoom.getAdmin().getUserName())
                    .build());
        });
        Collections.reverse(chatRooms);
        return chatRooms;
    }


    // 채팅방 만들 때 관리자와 방 번호, 이름 생성
    public ChatRoom createChatRoom(String roomName, Principal principal) {

        Member admin = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        System.out.println(admin);
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(roomName)
                .admin(admin)
                .build();

        chatRoomRepository.save(chatRoom);

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .chatroom(chatRoom)
                .member(admin)
                .build();

        memberChatRoomRepository.save(memberChatRoom);

        return chatRoom;
    }

    public void enterChatRoom(Long roomId, Principal principal) {

        Member member = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        boolean isUserInChatRoom = chatRoom.getMemberChatRooms().stream()
                .anyMatch(memberChatRoom -> memberChatRoom.getMember().getId().equals(member.getId()));

        if (!isUserInChatRoom) {
            MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                    .chatroom(chatRoom)
                    .member(member)
                    .build();

            memberChatRoomRepository.save(memberChatRoom);
        }

    }

    @Transactional
    public void saveChatMessage(ChatDTO chatDTO, Principal principal) {
        Member sender = memberRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        MemberChatRoom chatRoom = memberChatRoomRepository.findById(Long.parseLong(chatDTO.getRoomId()))
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));


        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatDTO.getMessage())
                .sender(sender)
                .chatRoom(chatRoom)
                .time(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

    }

    public ChatRoom findChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }
}