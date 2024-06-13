package com.example.selfduizoo.service;

import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.CustomMemberDetails;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import com.example.selfduizoo.repo.MemberRepo;
import com.example.selfduizoo.repo.ProfileImageRepo;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomMemberDetailsManager implements UserDetailsManager {
    private final MemberRepo memberRepo;
    private final ProfileImageRepo profileImageRepo;
    @Value("${spring.servlet.multipart.location}")
    String location;
    @Override
    @Transactional
    public void createUser(UserDetails user) {
        if (userExists(user.getUsername())){
            log.error("이미 존재하는 아이디입니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (user instanceof CustomMemberDetails memberDetails){

            Member member = Member.builder()
                    .userName(memberDetails.getUsername())
                    .password(memberDetails.getPassword())
                    .email(memberDetails.getEmail())
                    .authority(Authority.ROLE_INACTIVE_USER)
                    .build();
            //디렉토리 생성을 위한 path
            Path directoryPath = Paths.get(location + "/" + memberDetails.getUsername());
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
                    .url("/img/profile/"+memberDetails.getUsername()+"/defaultImg.PNG")
                    .member(member)
                    .build();

            log.info("Authority : {}", memberDetails.getAuthorities());
            if (userExists(member.getUserName())){
                log.error("이미 존재하는 유저명입니다.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
            memberRepo.save(member);
            profileImageRepo.save(profileImage);
        } else {
            throw new IllegalStateException("UserDetails user는 CustomMemberDetails가 아닙니다. 변수 / 메서드 등을 확인해주세요.");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepo.findByUserName(username);
        if (optionalMember.isEmpty()){
            log.info("유저명을 찾을 수 없습니다.");
            throw new UsernameNotFoundException(username);
        } else if (optionalMember.get().getAuthority().getAuthority().equals("휴면사용자")){
            log.info("비활성 사용자입니다.");
            throw new UsernameNotFoundException(username);
        }
        log.info("유저명 확인 : {}", optionalMember.get().getUserName());
        Member member = optionalMember.get();
        //DB에서 유저명으로 찾은 member를  customMemberDetails로 만들어준다.

        CustomMemberDetails customMemberDetails = CustomMemberDetails.builder()
                .userName(member.getUserName())
                .password(member.getPassword())
                .authority(member.getAuthority())
                .build();

        return customMemberDetails;
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void deleteUser(String username) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public boolean userExists(String username) {
        return memberRepo.existsByUserName(username);
    }

}
