package com.sparta.domain.concert;


import com.sparta.domain.entity.Timestamped;
import com.sparta.domain.seat.Seat;
import com.sparta.domain.ticket.Ticket;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "concerts")
public class Concert extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String concertName;
    private int price;
    private String description;
    private String image;
    private LocalDateTime date;
    private LocalDateTime startAt;

	@OneToMany(mappedBy = "concert", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Seat> seats = new ArrayList<>();

	@OneToMany(mappedBy = "concert", cascade = CascadeType.ALL)
	private List<Ticket> tickets = new ArrayList<>();


    public Concert(String concertName, int price, String description, String image,
        LocalDateTime date, LocalDateTime startAt) {
        this.concertName = concertName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.date = date;
        this.startAt = startAt;
    }

    public void updateConcert(String concertName, int price, String description, String image, LocalDateTime date, int seating) {
        this.concertName = concertName;
        this.price = price;
        this.description = description;
        this.image = image;
        this.date = date;
        // 좌석 정보 수정은 추가 구현 필요
    }

}