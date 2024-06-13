package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.ProfileImageDto;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import com.example.selfduizoo.repo.ProfileImageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileImgService {
    private final ProfileImageRepo profileImageRepo;

    public ProfileImageDto readProfileImage(Member member){
        ProfileImage profileImage = profileImageRepo.findByMember(member)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."
                        )
                );
        return ProfileImageDto.fromEntity(profileImage);

    }
}
