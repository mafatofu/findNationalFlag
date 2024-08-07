package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import com.example.selfduizoo.repo.MemberRepo;
import com.example.selfduizoo.repo.ProfileImageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepo memberRepo;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageRepo profileImageRepo;
    @Value("${spring.servlet.multipart.location}")
    String location;
    public MemberDto readMember(String userName, String loginMethod){
        //일반로그인 / 소셜로그인을 분기
        Member member = memberRepo.findByUserNameAndLoginMethodAndAuthorityNot(userName, loginMethod, Authority.ROLE_DORMANT_USER)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        return MemberDto.fromEntity(member);
    }

    public MemberDto readMemberForSocial(String userName){
        //소셜로그인
        Member member = memberRepo.findByUserNameAndLoginMethodAndAuthorityNot(userName, "social", Authority.ROLE_DORMANT_USER)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        return MemberDto.fromEntity(member);
    }

    public Member readMemberOriginal(String userName, String loginMethod){
        Member member = memberRepo.findByUserNameAndLoginMethodAndAuthorityNot(userName, loginMethod, Authority.ROLE_DORMANT_USER)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        return member;
    }

    //아이디 중복확인용
    public int duplicateCkForUserName(String userName) {
        //중복확인용
        int isDuplicated = 0;
        boolean isExist = memberRepo.existsByUserNameAndLoginMethodAndAuthorityNot(userName, "regualr", Authority.ROLE_DORMANT_USER);
        if (isExist)
            isDuplicated = 1;
        return isDuplicated;
    }

    //이메일 중복확인용
    public int duplicateCkForEmail(String email) {
        //중복확인용
        int isDuplicated = 0;
        boolean isExist = memberRepo.existsByEmailAndLoginMethodAndAuthorityNot(email, "regular", Authority.ROLE_DORMANT_USER);
        if (isExist)
            isDuplicated = 1;
        return isDuplicated;
    }

    //회원정보 수정
    @Transactional
    public void updateMember(MemberDto dto, MultipartFile imageFile) throws IOException {
        Member member = memberRepo.findByUserNameAndLoginMethodAndAuthorityNot(dto.getUserName(), dto.getLoginMethod(), Authority.ROLE_DORMANT_USER)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        ProfileImage profileImage = profileImageRepo.findByMember(member)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."
                        )
                );
        if (dto.getPassword() != null){
            member.changeMemberInfo(passwordEncoder.encode(dto.getPassword()));
        }

        if (imageFile != null){
            //기존 디렉토리의 이미지 파일 삭제
            String[] urlSplit = profileImage.getUrl().split("/");
            String urlSplitNamePart = urlSplit[urlSplit.length-1];
            File oldProfileImg = new File(location + "/" +member.getLoginMethod()+"/" + member.getUserName() + "/" + urlSplitNamePart);
            boolean oldProfileImgdelete = oldProfileImg.delete();
            //새롭게 파일 생성
            //랜덤이름
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + imageFile.getOriginalFilename();
            File profileImg = new File(location + "/" + member.getLoginMethod()+"/" + member.getUserName(), fileName);
            imageFile.transferTo(profileImg);
            //생성 파일명을 테이블에 저장
            profileImage.updateUrl("/img/profile/"+member.getLoginMethod()+"/"+member.getUserName()+"/"+fileName);
        }
    }
    //회원 탈퇴
    //휴면사용자로 권한 변경
    @Transactional
    public void deleteMember(String userName){
        Member member = memberRepo.findByUserName(userName)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        member.deleteMemberInfo();
    }


}
