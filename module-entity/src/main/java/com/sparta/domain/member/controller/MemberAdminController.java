package com.sparta.domain.member.controller;


import com.sparta.domain.member.dto.request.MemberRoleChangeRequest;
import com.sparta.domain.member.service.MemberAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberAdminController {

    private final MemberAdminService memberAdminService;

    @PatchMapping("/admin/members/{memberId}")
    public void changeMemberRole(@PathVariable Long memberId, @RequestBody MemberRoleChangeRequest memberRoleChangeRequest) {
        memberAdminService.changeMemberRole(memberId, memberRoleChangeRequest);
    }
}