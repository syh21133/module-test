package com.sparta.domain.concert.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ConcertRequest {
    private String concertName;
    private int price;
    private String description;
    private String image;
    private LocalDateTime date;
    private LocalDateTime startAt;
    private String seatLetterRange; // A-D
    private String seatNumberRange; // 1-5
}
