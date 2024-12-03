package com.sparta.domain.coupon.repository;

import com.sparta.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponRepositoryCustom {


    Page<CouponResponseDto> findByMember(Member member, Pageable pageable);
}
