package com.sparta.domain.coupon.repository;


import com.sparta.domain.coupon.Coupon;
import com.sparta.domain.coupon.Issuance;
import com.sparta.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuanceRepository extends JpaRepository<Issuance, Long> {

    Issuance findByCouponAndMember(Coupon coupon, Member member);

    Long countByCoupon(Coupon coupon);
}
