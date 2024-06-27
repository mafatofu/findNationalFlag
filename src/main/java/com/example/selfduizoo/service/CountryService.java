package com.example.selfduizoo.service;

import com.example.selfduizoo.dto.CountryDto;
import com.example.selfduizoo.entity.Country;
import com.example.selfduizoo.repo.CountryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryService {
    private final CountryRepo countryRepo;
    //국기 읽어오기
    public CountryDto readCountry(String countryName){
        Country country = countryRepo.findByCountryName(countryName)
                .orElseThrow(
                        ()-> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "국가를 찾을 수 없습니다."
                        )
                );
        return CountryDto.fromEntity(country);
    }
    //국가명 / 국기 중복체크
    public int duplicateCkForNewCountry(MultipartFile flagImg, String newCountryName){
        int isError = 0;
        //기존 국가명 / 국기명 중복 여부 확인
        boolean isExist = countryRepo.existsByCountryName(newCountryName);
        if (isExist){
            isError = 1;
        }
        return isError;
    }

    //국가 생성
    @Transactional
    public void insertCountry(String newCountryName, String fileName){
        Country country = Country.builder()
                .countryName(newCountryName)
                .flagPath("img/flag/" + fileName)
                .build();
        countryRepo.save(country);
    }
}
