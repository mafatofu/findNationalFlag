package com.example.selfduizoo.entity;

import lombok.Getter;

@Getter
public enum Authority {
    ROLE_DORMANT_USER("휴면사용자"),
    ROLE_INACTIVE_USER("비활성사용자"),
    ROLE_USER("일반사용자"),
    ROLE_ADMIN("관리자");

    private final String authority;
    Authority(String authority) {
        this.authority = authority;
    }
}
