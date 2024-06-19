package com.example.selfduizoo.repo;

import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepo extends JpaRepository<Member, Long> {
    Optional<Member> findByUserName(String userName);
    Optional<Member> findByUserNameAndAuthorityNot(String userName, Authority authority);
    Optional<Member> findByUserNameAndLoginMethodAndAuthorityNot(String userName, String loginMethod, Authority authority);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndLoginMethod(String email, String loginMethod);
    boolean existsByUserName(String userName);
    boolean existsByUserNameAndAuthorityNot(String userName, Authority authority);
    boolean existsByUserNameAndLoginMethodAndAuthorityNot(String userName, String loginMethod, Authority authority);
    boolean existsByEmail(String email);
    boolean existsByEmailAndAuthorityNot(String email, Authority authority);
    boolean existsByEmailAndLoginMethodAndAuthorityNot(String email, String loginMethod, Authority authority);
}
