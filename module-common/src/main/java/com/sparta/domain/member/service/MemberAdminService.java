package com.sparta.domain.member.service;


import com.sparta.domain.member.Member;
import com.sparta.domain.member.MemberRole;
import com.sparta.domain.member.dto.request.MemberRoleChangeRequest;
import com.sparta.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;

    @Transactional
    public void changeMemberRole(Long memberId, MemberRoleChangeRequest memberRoleChangeRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("User not found"));
        member.updateRole(MemberRole.of(memberRoleChangeRequest.getRole()));
    }
}