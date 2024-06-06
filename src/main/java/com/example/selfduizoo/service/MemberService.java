package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepo memberRepo;
    private final PasswordEncoder passwordEncoder;
    public MemberDto readMember(String userName){
        Member member = memberRepo.findByUserName(userName)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        return MemberDto.fromEntity(member);
    }

    //아이디 중복확인용
    public int duplicateCkForUserName(String userName) {
        //중복확인용
        int isDuplicated = 0;
        boolean isExist = memberRepo.existsByUserName(userName);
        if (isExist)
            isDuplicated = 1;
        return isDuplicated;
    }

    //이메일 중복확인용
    public int duplicateCkForEmail(String email) {
        //중복확인용
        int isDuplicated = 0;
        boolean isExist = memberRepo.existsByEmail(email);
        if (isExist)
            isDuplicated = 1;
        return isDuplicated;
    }

    //회원정보 수정
    @Transactional
    public void updateMember(String userName, String password){
        Member member = memberRepo.findByUserName(userName)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );
        member.changeMemberInfo(passwordEncoder.encode(password));
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

    //메일 인증 저장

}
