package com.sparta.domain.ticket.dto.response;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketResponse {
    private String concertTitle;
    private String bookedBy;
    private String seatNumber;
    private LocalDateTime date;
    private boolean isCancelled;

    public TicketResponse(String concertTitle, String seatNumber, LocalDateTime date) {
        this.concertTitle = concertTitle;
        this.seatNumber = seatNumber;
        this.date = date;
    }

}

