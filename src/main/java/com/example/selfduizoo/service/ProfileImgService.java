package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.ProfileImageDto;
import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import com.example.selfduizoo.repo.MemberRepo;
import com.example.selfduizoo.repo.ProfileImageRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileImgService {
    private final ProfileImageRepo profileImageRepo;
    private final MemberRepo memberRepo;
    @Value("${spring.servlet.multipart.location}")
    String location;

    public ProfileImageDto readProfileImage(Member member){
        ProfileImage profileImage = profileImageRepo.findByMember(member)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."
                        )
                );
        return ProfileImageDto.fromEntity(profileImage);

    }
    public void createProfileDirectory(Member member){
        //디렉토리 생성을 위한 path
        Path directoryPath = Paths.get(location + "/" +member.getLoginMethod()+ "/" + member.getUserName());
        try {
            //프로필이미지 디렉토리 생성
            Files.createDirectory(directoryPath);
            log.info("디렉토리 생성 : {}", directoryPath);
        } catch (FileAlreadyExistsException e){
            log.info("디렉토리가 이미 존재합니다");
        } catch (NoSuchFileException e){
            log.info("디렉토리 경로가 존재하지 않습니다.");
        } catch (IOException e){
            e.printStackTrace();
        }
        //디폴트 이미지 복사
        File defaultImg = new File(location, "/defaultImg.PNG");
        File copyImg = new File(directoryPath.toString(), "/defaultImg.PNG");
        try {
            FileCopyUtils.copy(defaultImg, copyImg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //회원가입시 기본 프로필이미지 추가
        ProfileImage profileImage = ProfileImage.builder()
                .url("/img/profile/"+member.getLoginMethod()+"/"+member.getUserName()+"/defaultImg.PNG")
                .member(member)
                .build();

        log.info("Authority : {}", member.getAuthority().getAuthority());
        profileImageRepo.save(profileImage);
    }
}
