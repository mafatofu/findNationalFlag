package com.example.selfduizoo.controller;

import com.example.selfduizoo.config.AuthenticationFacade;
import com.example.selfduizoo.dto.MemberDto;
import com.example.selfduizoo.dto.ProfileImageDto;
import com.example.selfduizoo.entity.CustomMemberDetails;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.entity.ProfileImage;
import com.example.selfduizoo.service.CustomMemberDetailsManager;
import com.example.selfduizoo.service.MemberService;
import com.example.selfduizoo.service.ProfileImgService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/findNationalFlag")
public class MemberController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authFacade;
    private final MemberService memberService;
    private final ProfileImgService profileImgService;
    private final CustomMemberDetailsManager customManager;
    private static String msg = "";
    @GetMapping
    public String home(
            Model model
    ){
        MemberDto memberDto = memberService.readMember(authFacade.getAuth().getName());
        model.addAttribute("member", memberDto);
        return "member/home";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "member/loginForm";
    }
    @PostMapping("/login")
    public String login(){
        return "redirect:/findNationalFlag";
    }
    @PostMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes){
        msg = "로그아웃 되셨습니다.";
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:/findNationalFlag";
    }
    @GetMapping("/joinForm")
    public String joinForm(){
        return "member/joinForm";
    }
    @PostMapping("/join")
    public ResponseEntity<MemberDto> join(
            @RequestBody
            MemberDto dto,
            RedirectAttributes redirectAttributes
    ){
        customManager.createUser(CustomMemberDetails.builder()
                .userName(dto.getUserName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build());
        msg = "회원가입 되었습니다 ^^.";
        redirectAttributes.addFlashAttribute("msg", msg);
        return ResponseEntity.ok().body(dto);
    }
    //이메일 중복확인
    @PostMapping("/duplicateCkForEmail")
    @ResponseBody
    public int duplicateCkForEmail(
            @RequestParam("email")
            String email
    ){
        return memberService.duplicateCkForEmail(email);
    }

    //유저 id 중복확인
    @PostMapping("/duplicateCkForUserName")
    @ResponseBody
    public int duplicateCkForUserName(
            @RequestParam("userName")
            String userName
    ){
        return memberService.duplicateCkForUserName(userName);
    }

    //회원정보 수정 페이지
    @GetMapping("/profile")
    public String updateProfile(
            Model model
    ){
        MemberDto memberDto = memberService.readMember(authFacade.getAuth().getName());
        Member member = memberService.readMemberOriginal(authFacade.getAuth().getName());
        ProfileImageDto profileImageDto = profileImgService.readProfileImage(member);
        model.addAttribute("member", memberDto);
        model.addAttribute("profileImage", profileImageDto);
        return "member/myProfile";
    }

    //회원정보 수정
    @PostMapping(value = "/updateProfile")
    @ResponseBody
    public void updateProfile(
            @RequestPart(value = "key") MemberDto dto,
            @RequestPart(value ="profileImage") MultipartFile profileImage
    ) throws IOException {
        memberService.updateMember(dto.getUserName(), dto.getPassword(), profileImage);

    }

    //회원탈퇴
    @PostMapping("/deleteProfile")
    @ResponseBody
    public int deleteProfile(
            @RequestParam("userName")
            String userName,
            HttpServletResponse response,
            HttpServletRequest request
    ){
        //세션 삭제
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }
        session.invalidate();

        // + 쿠키 삭제
        // 같은 쿠키가 이미 존재하면 덮어쓰기 된다.
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0); // 0초 생존 -> 삭제
        cookie.setPath("/"); // 쿠키의 경로 설정
        response.addCookie(cookie); // 요청 객체를 통해서 클라이언트에게 전달

        memberService.deleteMember(userName);
        return 1;
    }



}
