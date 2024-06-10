package com.example.selfduizoo.controller;

import com.example.selfduizoo.dto.CountryDto;
import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
