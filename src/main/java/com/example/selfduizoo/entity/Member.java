package com.example.selfduizoo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

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

    //회원정보 수정
    public void changeMemberInfo(String password){
        this.password = password;
    }
}
