package com.sparta.domain.ticket;


import com.sparta.domain.concert.Concert;
import com.sparta.domain.entity.Timestamped;
import com.sparta.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "tickets")
public class Ticket extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id")
	private Concert concert;

	private String seatNumber;
	private LocalDateTime date;
	private boolean isCancelled;  // 취소 여부를 관리하는 필드

	public Ticket(Member member, Concert concert, String seatNumber, LocalDateTime date, boolean isCancelled) {
		this.member = member;
		this.concert = concert;
		this.seatNumber = seatNumber;
		this.date = date;
		this.isCancelled = false; // 기본적으로 티켓은 취소되지 않은 상태로 시작
	}

	public void cancel() {
		if (this.isCancelled) {
			throw new IllegalStateException("이 티켓은 이미 취소된 상태입니다.");
		}
		this.isCancelled = true;  // 취소 상태로 변경
	}


}
