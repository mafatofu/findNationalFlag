package com.example.selfduizoo.dto;

import com.example.selfduizoo.entity.Country;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    private Long id;
    private String countryName;
    private String flagPath;

    public static CountryDto fromEntity(Country entity){
        return CountryDto.builder()
                .id(entity.getId())
                .countryName(entity.getCountryName())
                .flagPath(entity.getFlagPath())
                .build();
    }
}
