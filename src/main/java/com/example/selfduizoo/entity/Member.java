package com.example.selfduizoo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert //insert 시 명시해주지 않아도 기본값 적용되도록
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userName")
    private String userName;
    private String password;
    private String email;
    private Authority authority;
    //로그인방법
    private String loginMethod;
    //프로필이미지
    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private ProfileImage profileImage;
    //메일인증관련
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MemberAuthenticationCodeEntity> mailAuth;
    //회원정보 수정
    public void changeMemberInfo(String password){
        this.password = password;
    }
    //회원정보 수정
    public void changeMemberInfo(Authority authority){
        this.authority = authority;
    }
    //소셜로그인용 엔티티 return
    public Member changeMemberInfoForSocial(String userName){
        this.userName = userName;
        return this;
    }
    public String getAuthorityKey(){
        return this.getAuthority().getAuthority();
    }


    //회원 탈퇴
    public void deleteMemberInfo(){
        this.authority = Authority.ROLE_DORMANT_USER;
    }

}
