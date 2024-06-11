package com.example.selfduizoo.dto;

import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String userName;
    private String password;
    private String email;
    private Authority authority;

    public static MemberDto fromEntity(Member entity){
        return MemberDto.builder()
                .userName(entity.getUserName())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .authority(entity.getAuthority())
                .build();
    }
}
