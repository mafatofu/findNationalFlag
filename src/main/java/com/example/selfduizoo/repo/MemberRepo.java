package com.example.selfduizoo.repo;

import com.example.selfduizoo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {
    Optional<Member> findByUserName(String userName);
    Optional<Member> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
