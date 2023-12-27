package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Comment;
import com.korea.project2_team4.Model.Entity.Member;
import com.korea.project2_team4.Model.Entity.Post;
import com.korea.project2_team4.Model.Entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 작성자(프로필)를 기반으로 페이징하여 댓글을 찾는 메서드
    Page<Comment> findByAuthor(Profile author, Pageable pageable);

    Page<Comment> findByLikeMembers(Member member, Pageable pageable);
}
