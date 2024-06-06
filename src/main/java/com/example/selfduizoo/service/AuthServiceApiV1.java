package com.example.selfduizoo.service;

import com.example.selfduizoo.config.AuthenticationFacade;
import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.dto.ReqAuthenticateCodeApiV1DTO;
import com.example.selfduizoo.dto.ReqSendEmailAuthenticationApiV1DTO;
import com.example.selfduizoo.dto.ResDTO;
import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.MemberAuthenticationCodeEntity;
import com.example.selfduizoo.repo.MemberAuthenticationCodeRepository;
import com.example.selfduizoo.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceApiV1 {
    private final MemberAuthenticationCodeRepository memberAuthenticationCodeRepository;

    private final EmailService emailService;
    private final MemberService memberService;
    private final AuthenticationFacade authFacade;
    private final MemberRepo memberRepo;
    @Transactional
    public HttpEntity<?> sendEmailAuthentication(
            ReqSendEmailAuthenticationApiV1DTO reqSendEmailAuthenticationApiV1DTO) {



        // 랜덤 인증 코드 생성해서
        String authenticationCode = createAuthenticationCode();

        // emailService의 sendEmailAuthenticationCode함수로 메일을 발송하고, 성공 여부에 따라 true / false 반환
        if (!emailService.sendEmailAuthentication(reqSendEmailAuthenticationApiV1DTO, authenticationCode)) {
            // 메일 발송 실패 시 BAD_REQUEST 반환
            return new ResponseEntity<>(
                    ResDTO.builder()
                            .code(-1)
                            .message("인증 번호 발송 실패")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        // 메일 발송 성공 시
        // 아직 유효한 인증 코드 데이터를 찾아서
        Optional<MemberAuthenticationCodeEntity> beforeMemberAuthenticationCodeEntityOptional = memberAuthenticationCodeRepository
                .findByEmailAndEndDateAfterAndDeleteDateIsNull(
                        reqSendEmailAuthenticationApiV1DTO.getEmail(),
                        LocalDateTime.now());

        // 있으면 무효화 (delete_date 설정)
        if (beforeMemberAuthenticationCodeEntityOptional.isPresent()) {
            MemberAuthenticationCodeEntity beforeMemberAuthenticationCodeEntity = beforeMemberAuthenticationCodeEntityOptional
                    .get();
            beforeMemberAuthenticationCodeEntity.setDeleteDate(LocalDateTime.now());
            memberAuthenticationCodeRepository.save(beforeMemberAuthenticationCodeEntity);
        }
        //메일 인증 엔티티에 넣을 member entity 불러오기
        Member member = memberRepo.findByUserName(authFacade.getAuth().getName())
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                        )
                );

        // 인증 코드 데이터를 저장하기 위해 새 엔티티를 작성하여
        MemberAuthenticationCodeEntity memberAuthenticationCodeEntity = MemberAuthenticationCodeEntity
                .builder()
                .email(reqSendEmailAuthenticationApiV1DTO.getEmail())
                .code(authenticationCode)
                .isVerified(false)
                .endDate(LocalDateTime.now().plus(5, ChronoUnit.MINUTES))
                .createDate(LocalDateTime.now())
                .member(member)
                .build();

        // 저장
        memberAuthenticationCodeRepository.save(memberAuthenticationCodeEntity);

        return new ResponseEntity<>(
                ResDTO.builder()
                        .code(0)
                        .message("인증 번호 발송 성공")
                        .build(),
                HttpStatus.OK);
    }

    // 랜덤 인증번호 생성 함수
    public String createAuthenticationCode() {
        // 8자리, 문자, 숫자 포함 문자열 생성
        return RandomStringUtils.random(8, true, true);
    }

    //인증코드 검증 로직
    @Transactional
    public HttpEntity<?> authenticateCode(ReqAuthenticateCodeApiV1DTO reqAuthenticateCodeApiV1DTO) {

        // 유효한 인증 코드 데이터를 찾아서
        Optional<MemberAuthenticationCodeEntity> memberAuthenticationCodeEntityOptional = memberAuthenticationCodeRepository
                .findByEmailAndEndDateAfterAndDeleteDateIsNull(reqAuthenticateCodeApiV1DTO.getEmail(),
                        LocalDateTime.now());

        // 없으면 인증 코드 없음 반환
        if (memberAuthenticationCodeEntityOptional.isEmpty()) {
            return new ResponseEntity<>(
                    ResDTO.builder()
                            .code(-1)
                            .message("인증 코드 없음")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

        // 있으면 찾아서
        MemberAuthenticationCodeEntity memberAuthenticationCodeEntity = memberAuthenticationCodeEntityOptional.get();

        // 해당 entity의 인증 코드와 입력한 인증 코드가 일치하는 지 검증
        if (memberAuthenticationCodeEntity.getCode().equals(reqAuthenticateCodeApiV1DTO.getCode())) {
            // 인증 성공 처리
            memberAuthenticationCodeEntity.setVerified(true);
            //메일 인증 엔티티에 넣을 member entity 불러오기
            Member member = memberRepo.findByUserName(authFacade.getAuth().getName())
                    .orElseThrow(
                            ()-> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
                            )
                    );
            //member 권한 비활성 -> 일반 사용자로 변경
            member.changeMemberInfo(Authority.ROLE_USER);

            return new ResponseEntity<>(
                    ResDTO.builder()
                            .code(0)
                            .message("인증 성공")
                            .build(),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    ResDTO.builder()
                            .code(-1)
                            .message("인증 실패")
                            .build(),
                    HttpStatus.BAD_REQUEST);
        }

    }
}
