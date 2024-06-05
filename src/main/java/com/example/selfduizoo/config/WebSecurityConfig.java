package com.example.selfduizoo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                //사이트간 요청위조 방지
                //Get이 아닌 요청 시 csrf토큰여부를 확인
                //CsrfToken을 쿠키에 보관하여 JavaScript 기반 응용 프로그램을 지원
//                .csrf((csrf) -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(
                        auth -> auth
                        .requestMatchers(
                                //메인
                                "/findNationalFlag",
                                //로그인
                                "/findNationalFlag/login",
                                //회원가입
                                "/findNationalFlag/joinForm",
                                "/findNationalFlag/join",
                                "/findNationalFlag/duplicateCkForUserName",
                                "/findNationalFlag/duplicateCkForEmail",

                                //메일 관련
                                "/findNationalFlag/api/v1/auth/email-authentication",

                                //템플릿 관련
                                "/css/**",
                                "/js/**",
                                "/fonts/**",
                                "/img/**"
                        ).permitAll()
                                .requestMatchers(
                                        //국가이름검색
                                        "/findNationalFlag/searchFlag",
                                        //로그아웃
                                        "/findNationalFlag/logout",
                                        //마이페이지
                                        "/findNationalFlag/profile",
                                        //회원정보 수정
                                        "/findNationalFlag/updateProfile",
                                        //회원 탈퇴
                                        "/findNationalFlag/deleteProfile"
                                ).authenticated()
                )
                .formLogin(
                        formLogin -> formLogin
                        //로그인 url
                        .loginPage("/findNationalFlag/login")
                        //로그인 성공 시 이동할 url
                        .defaultSuccessUrl("/findNationalFlag")
                        //로그인 진행할 url
                        .loginProcessingUrl("/findNationalFlag/login")
                        //로그인 성공 핸들러
                        .successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                //인증 성공 시
                                response.sendRedirect("/findNationalFlag");
                            }
                        })
                        //실패시 
                        .failureUrl("/findNationalFlag/login?fail").permitAll()
                )
                //로그아웃처리
                .logout(
                        logout -> logout
                        //로그아웃 url
                        .logoutUrl("/findNationalFlag/logout")
                        //로그아웃 성공 시 이동할 url
                        .logoutSuccessUrl("/findNationalFlag").invalidateHttpSession(true)// http 세션 무효화 여부
                        .deleteCookies("JSESSIONID") //해당 쿠키 삭제
                        .permitAll()
                );





        return http.build();
    }
}
