package com.example.selfduizoo.controller;

import com.example.selfduizoo.config.AuthenticationFacade;
import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final AuthenticationFacade authFacade;
    private final MemberService memberService;
    //소셜로그인
    @GetMapping("/")
    public String mainHome(
    ){
        return "redirect:/findNationalFlag";
    }
}
