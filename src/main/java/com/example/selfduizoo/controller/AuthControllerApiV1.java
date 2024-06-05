package com.example.selfduizoo.controller;

import com.example.selfduizoo.dto.ReqSendEmailAuthenticationApiV1DTO;
import com.example.selfduizoo.service.AuthServiceApiV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/findNationalFlag/api/v1/auth")
public class AuthControllerApiV1 {
    private final AuthServiceApiV1 authServiceApiV1;
    // 이메일 인증 번호 요청
    @PostMapping("/email-authentication")
    @ResponseBody
    public HttpEntity<?> sendEmailAuthentication(
            @RequestBody ReqSendEmailAuthenticationApiV1DTO reqSendEmailAuthenticationApiV1DTO) {
        return authServiceApiV1.sendEmailAuthentication(reqSendEmailAuthenticationApiV1DTO);
    }
}
