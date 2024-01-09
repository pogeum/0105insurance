package com.korea.project2_team4.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korea.project2_team4.Model.Dto.ChatDTO;
import com.korea.project2_team4.Model.Dto.ChatRoom;
import com.korea.project2_team4.Model.Entity.ChatMessage;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Repository.ChatRepository;
import com.korea.project2_team4.Repository.MemberRepository;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    // 전체 채팅방 조회
    // 채팅방 생성 순서를 최근 순으로 반환
    public List<ChatRoom> findAllRoom() {
        List chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    // roomID 기준으로 채팅방 찾기
    public ChatRoom findRoomById (String roomId) {
        return chatRoomMap.get(roomId);
    }

    // roomName 으로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName) {
        //채팅룸 이름으로 채팅방을 먼저 생성한다
        ChatRoom chatRoom = new ChatRoom().create(roomName);

        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장한다.
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);

        return chatRoom;
    }

    // 채팅방 인원 +1
    public void plusUserCnt(String roomId) {
        ChatRoom room = chatRoomMap.get(roomId);

        room.setUserCount(room.getUserCount()+1);
    }

    // 채팅방 인원 -1
    public void minusUserCnt(String roomId) {
        ChatRoom room = chatRoomMap.get(roomId);

        room.setUserCount(room.getUserCount()-1);
    }

    // 채팅방 유저리스트에 유저 추가하기
    public String addUser(String roomId, String userName) {
        ChatRoom room = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();

        // 아이디 중복 확인 후 userList에 추가
        room.getUserList().put(userUUID, userName);

        return userUUID;
    }

    // 채팅방 유저 이름 중복 확인
    public String isDuplicateUserName(String roomId, String userName) {
        ChatRoom room = chatRoomMap.get(roomId);
        String tmp = userName;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserList 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기

        while (room.getUserList().containsValue(tmp)) {
            int ranNum = (int) (Math.random()*100)+1;

            tmp = userName+ranNum;
        }

        return tmp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID) {
        ChatRoom room = chatRoomMap.get(roomId);
        room.getUserList().remove(userUUID);
    }

    // 채팅방 유저이름 조회
    public String getUserName(String roomId, String userUUID) {
        ChatRoom room = chatRoomMap.get(roomId);
        return room.getUserList().get(userUUID);
    }

    // 채팅방 전체 userList 조회
    // getUserList 의 hashMap 을 for 문을 돌린 후
    // value 값만 뽑아내서 list 에 저장 후 return
    public ArrayList<String> getUserList(String roomId) {
        ArrayList<String> list = new ArrayList<>();

        ChatRoom room = chatRoomMap.get(roomId);

        room.getUserList().forEach((key, value) -> list.add(value));
        return list;
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