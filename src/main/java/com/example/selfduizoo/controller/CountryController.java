package com.example.selfduizoo.controller;

import com.example.selfduizoo.dto.CountryDto;
import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.dto.insertCountryDto;
import com.example.selfduizoo.entity.Country;
import com.example.selfduizoo.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/findNationalFlag/country")
public class CountryController {
    private final CountryService countryService;

    //국가 검색 결과
    @ResponseBody
    @GetMapping("/searchFlag")
    public ResponseEntity<CountryDto> searchFlag(
            @RequestParam("countryName")
            String countryName
    ){
        CountryDto dto = countryService.readCountry(countryName);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping(value = "/insertFlag")
    @ResponseBody
    public int insertFlag(
            @RequestPart(value = "flagImg")MultipartFile flagImg,
            @RequestPart(value = "countryData", required = false) insertCountryDto dto
    ) throws IOException {
        //중복된 기존 국가명 / 국기명이 없을 시 파일 생성
        int result = countryService.duplicateCkForNewCountry(flagImg, dto.getNewCountryName());
        if (result == 0){
            //파일 생성
            if (flagImg != null){
                UUID uuid = UUID.randomUUID();
                String fileName =  uuid.toString() + "_" + flagImg.getOriginalFilename();
                File newFlag = new File("C://springboot/selfduizoo/src/main/resources/static/img/flag" + "/", fileName);
                flagImg.transferTo(newFlag);
                //국가 등록
                countryService.insertCountry(dto.getNewCountryName(), fileName);
            }
        }
        return result;
    }
}
