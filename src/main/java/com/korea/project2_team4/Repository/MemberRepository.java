package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserName(String username);

    Optional<Member> findByRealNameAndPhoneNum(String realName, String phoneNum);
    Optional<Member> findByRealNameAndEmail(String realName, String phoneNum);

    boolean existsByEmail(String email);
}
