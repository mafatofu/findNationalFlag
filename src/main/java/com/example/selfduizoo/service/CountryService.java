package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.CountryDto;
import com.example.selfduizoo.entity.Country;
import com.example.selfduizoo.repo.CountryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {
    private final CountryRepo countryRepo;

    public CountryDto readCountry(String countryName){
        Country country = countryRepo.findByCountryName(countryName)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "국가를 찾을 수 없습니다."
                        )
                );
        return CountryDto.fromEntity(country);
    }
}
