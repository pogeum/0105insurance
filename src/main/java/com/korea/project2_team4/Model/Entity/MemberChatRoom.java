package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class MemberChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 다대일 관계 설정
    @ManyToOne
    private Member member;

    // 다대일 관계 설정
    @ManyToOne
    private ChatRoom chatroom;

    @Builder
    public MemberChatRoom(Long id, Member member, ChatRoom chatroom){
        this.id = id;
        this.changeMember(member);
        this.changeChatRoom(chatroom);

    }

    public MemberChatRoom() {
        //있어야 회원이 여러개의 방을 만들 때 에러가 안나고 db저장도 잘 된다.
    }

    public void changeMember(Member member){
        this.member = member;
        member.getMemberChatRooms().add(this);
    }
    public void changeChatRoom(ChatRoom chatroom){
        this.chatroom = chatroom;
        chatroom.getMemberChatRooms().add(this);
    }
}
