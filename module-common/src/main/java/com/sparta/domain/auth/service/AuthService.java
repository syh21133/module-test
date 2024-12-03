package com.sparta.domain.auth.service;


import com.sparta.config.JwtUtil;
import com.sparta.domain.auth.dto.SignupRequest;
import com.sparta.domain.auth.dto.SignupResponse;
import com.sparta.domain.member.Member;
import com.sparta.domain.member.MemberRole;
import com.sparta.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        MemberRole memberRole = MemberRole.of(signupRequest.getMemberRole());

        Member newUser = new Member(
                signupRequest.getEmail(),
                signupRequest.getName(),
                encodedPassword,
                memberRole
        );
        Member savedUser = memberRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), memberRole);

        return new SignupResponse(bearerToken);
    }

}