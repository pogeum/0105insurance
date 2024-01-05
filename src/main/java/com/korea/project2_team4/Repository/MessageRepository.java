package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
