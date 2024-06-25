package com.example.selfduizoo.service;

import com.example.selfduizoo.config.OAuthAttributes;
import com.example.selfduizoo.entity.Authority;
import com.example.selfduizoo.entity.Member;
import com.example.selfduizoo.repo.MemberRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

//소셜로그인서비스
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepo memberRepo;
    private final HttpSession httpSession;
    private final ProfileImgService profileImgService;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuthAttributes attributes2 = OAuthAttributes.of(
                registrationId, userNameAttributeName, oAuth2User.getAttributes());
        //소셜로그인을 통해 끌고온 유저 정보 / 어떤 소셜로그인을 사용했는지
        Member member = saveOrUpdate(attributes2, userRequest.getClientRegistration().getRegistrationId().toString());

        httpSession.setAttribute("member", member);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getAuthorityKey())),
                attributes,
                userNameAttributeName
        );
    }

    private Member saveOrUpdate(OAuthAttributes attributes, String loginMethod){
        Optional<Member> optionalMember = memberRepo.findByEmailAndLoginMethod(attributes.getEmail(), loginMethod);
        Member member = null;
        if (optionalMember.isPresent()){
            member = optionalMember.get();
            member.changeMemberInfoForSocial(attributes.getName());
        } else {
            member = attributes.toEntity(loginMethod);
            profileImgService.createProfileDirectory(member);
        }

        return memberRepo.save(member);
    }
}
