package com.sparta.domain.concert.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertUpdateRequest {
    private String concertName;
    private int price;
    private String description;
    private String image;
    private LocalDateTime date;
    private int seating;
}
