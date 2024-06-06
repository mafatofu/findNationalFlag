package com.example.selfduizoo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//인증 코드 검증 데이터 받을 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqAuthenticateCodeApiV1DTO {

    private String email;

    private String code;
}