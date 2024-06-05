package com.example.selfduizoo.repo;

import com.example.selfduizoo.entity.MemberAuthenticationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface MemberAuthenticationCodeRepository extends JpaRepository<MemberAuthenticationCodeEntity, Long> {
    // 이메일로 end_date가 지금 이후고, delete_date가 null인 데이터 찾아오기
    Optional<MemberAuthenticationCodeEntity> findByEmailAndEndDateAfterAndDeleteDateIsNull(String email, LocalDateTime currentDateTime);
}
