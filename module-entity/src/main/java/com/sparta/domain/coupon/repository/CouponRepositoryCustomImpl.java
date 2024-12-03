package com.sparta.domain.coupon.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.domain.coupon.QIssuance;
import com.sparta.domain.coupon.dto.response.CouponResponseDto;
import com.sparta.domain.member.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.sparta.domain.coupon.QCoupon.coupon;
import static com.sparta.domain.coupon.QIssuance.issuance;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CouponResponseDto> findByMember(Member member, Pageable pageable) {

        List<CouponResponseDto> result = queryFactory
            .select(Projections.constructor(CouponResponseDto.class,
                coupon.id,
                coupon.couponName,
                coupon.count,
                coupon.discount
            ))
            .from(coupon)
            .join(issuance).on(coupon.id.eq(issuance.coupon.id))
            .where(issuance.member.id.eq(member.getId()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory
            .select(coupon.count())
            .from(coupon)
            .join(issuance).on(coupon.id.eq(issuance.coupon.id))
            .where(issuance.member.id.eq(member.getId()))
            .fetchOne();

        return new PageImpl<>(result, pageable, total);

    }

}
