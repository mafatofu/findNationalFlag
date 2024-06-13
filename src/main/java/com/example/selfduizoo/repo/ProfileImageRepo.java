package com.example.selfduizoo.repo;

import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepo extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByMember(Member member);
}
