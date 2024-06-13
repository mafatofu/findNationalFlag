package com.example.selfduizoo.dto;

import com.example.selfduizoo.entity.ProfileImage;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageDto {
    private String url;

    public static ProfileImageDto fromEntity(ProfileImage entity){
        return ProfileImageDto.builder()
                .url(entity.getUrl())
                .build();
    }
}
