package com.example.selfduizoo.dto;

import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String userName;
    private String password;
    private String email;
    private String loginMethod;
    private Authority authority;
    private MultipartFile profileImage;

    public static MemberDto fromEntity(Member entity){
        return MemberDto.builder()
                .userName(entity.getUserName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .loginMethod(entity.getLoginMethod())
                .authority(entity.getAuthority())
                .build();
    }
}
