package com.sparta.domain.ticket.lock.util;

public class RedisKeyUtil {

    public static String generateSeatLockKey(Long concertId, String seatNumber) {
        return String.format("CONCERT-%d-SEAT-%s", concertId, seatNumber);
    }

    public static String generateCouponLockKey(Long couponId) {
        return String.format("COUPON-%d-SEAT", couponId);
    }
}
