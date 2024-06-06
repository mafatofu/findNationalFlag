package com.example.selfduizoo.entity;

import com.example.selfduizoo.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

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

    //회원 탈퇴
    public void deleteMemberInfo(){
        this.authority = Authority.ROLE_DORMANT_USER;
    }

}
