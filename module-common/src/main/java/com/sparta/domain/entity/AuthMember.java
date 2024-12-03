package com.sparta.domain.entity;


import com.sparta.domain.member.MemberRole;
import lombok.Getter;

@Getter
public class AuthMember {

    private final Long id;
    private final String email;
    private final MemberRole userRole;

    public AuthMember(Long id, String email,MemberRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}