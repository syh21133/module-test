package com.sparta.domain.ticket.service;


import com.sparta.domain.concert.Concert;
import com.sparta.domain.concert.repository.ConcertRepository;
import com.sparta.domain.member.Member;
import com.sparta.domain.member.repository.MemberRepository;
import com.sparta.domain.seat.Seat;
import com.sparta.domain.seat.repository.SeatRepository;
import com.sparta.domain.ticket.Ticket;
import com.sparta.domain.ticket.dto.request.TicketingRequest;
import com.sparta.domain.ticket.dto.response.SeatResponse;
import com.sparta.domain.ticket.dto.response.TicketDetailResponse;
import com.sparta.domain.ticket.dto.response.TicketResponse;
import com.sparta.domain.ticket.lock.util.RedisKeyUtil;
import com.sparta.domain.ticket.repository.TicketRepository;
import com.sparta.security.UserDetailsImpl;
import com.sun.jdi.request.InvalidRequestStateException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {
	private final MemberRepository memberRepository;
	private final TicketRepository ticketRepository;
	private final ConcertRepository concertRepository;
	private final SeatRepository seatRepository;
	private final RedisTemplate<String, Long> redisTemplate;

	public List<SeatResponse> getAvailableSeats(Long concertId) {
		List<Seat> seats = seatRepository.findByConcertId(concertId);

		return seats.stream()
			.filter(seat -> !seat.isBooked())
			.map(seat -> new SeatResponse(seat.getSeatNumber(), seat.isBooked()))
			.collect(Collectors.toList());
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void selectSeat(Long concertId, String seatNumber, String name) {
		//좌석 확인
		List<Seat> seats = seatRepository.findAllByConcertIdAndSeatNumber(concertId, seatNumber);
		if (seats.size() != 1) {
			throw new IllegalArgumentException("좌석이 유효하지 않거나 중복된 데이터가 존재합니다.");
		}

		Seat seat = seats.get(0);

		if (!seat.isBooked()) {
			// 임의의 지연 추가 (동시성 문제를 유발하기 위함)
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			seat.book(name); // 좌석 예약
			seatRepository.save(seat); // 예약 정보 저장
		} else {
			throw new IllegalStateException("이미 예약된 좌석입니다.");
		}
	}

	@Transactional
	public TicketResponse createTicket(Long concertId, TicketingRequest request, String userName) {
		//좌석 확인
		List<Seat> seats = seatRepository.findAllByConcertIdAndSeatNumber(concertId, request.getSeatNumber());
		if (seats.size() != 1) {
			throw new IllegalArgumentException("좌석이 유효하지 않거나 중복된 데이터가 존재합니다.");
		}

		Seat seat = seats.get(0);

		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공연 입니다."));

		if (!seat.isBooked() || !userName.equals(seat.getBookedBy())) {
			throw new IllegalArgumentException("좌석이 예약 상태가 아니거나, 좌석에 예약된 예약자와 다릅니다.");
		}

		Member member = memberRepository.findByName(userName)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않는 유저입니다."));
		Ticket ticket = new Ticket(member, concert, seat.getSeatNumber(), request.getDate(), false);
		ticketRepository.save(ticket);

		String lockKey = RedisKeyUtil.generateSeatLockKey(concertId, request.getSeatNumber());
		releaseLock(lockKey);

		return new TicketResponse(
			concert.getConcertName(),
			member.getName(),
			seat.getSeatNumber(),
			request.getDate(),
			ticket.isCancelled()
		);
	}

	@Transactional
	public void deleteTicket(UserDetailsImpl authMember, Long ticketId, Long concertId) {
		Member member = memberRepository.findById(authMember.getMember().getId())
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
		Concert concert = concertRepository.findById(concertId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공연 입니다."));
		Ticket ticket = ticketRepository.findById(ticketId)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않는 티켓입니다."));

		if (ticket.isCancelled()) {
			throw new InvalidRequestStateException("이미 취소된 티켓입니다.");
		}
		if (!ticket.getMember().equals(member)) {
			throw new InvalidRequestStateException("로그인한 사용자의 티켓이 아닙니다.");
		}

		ticket.cancel();
		ticketRepository.save(ticket);
	}

	@Transactional(readOnly = true)
	public Page<TicketDetailResponse> getAllTickets(UserDetailsImpl authMember, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		Page<Ticket> tickets = ticketRepository.findByMemberId(authMember, pageable);

		return tickets.map(ticket -> new TicketDetailResponse(
				ticket.getId(),
				ticket.getConcert().getConcertName(),
				ticket.getSeatNumber(),
				ticket.getDate(),
				ticket.isCancelled(),
				authMember.getMember().getName()
		));
	}

	public TicketDetailResponse getTickets(UserDetailsImpl authMember, Long ticketId) {
		Member member = memberRepository.findById(authMember.getMember().getId())
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않는 티켓입니다."));

		return new TicketDetailResponse(
				ticket.getId(),
				ticket.getConcert().getConcertName(),
				ticket.getSeatNumber(),
				ticket.getDate(),
				ticket.isCancelled(),
				authMember.getMember().getName()
		);
	}

	//Lock 적용 좌석선택
	@Transactional
	public void selectSeatWithLock(Long concertId, String seatNumber, Long memberId) {
		String lockKey = RedisKeyUtil.generateSeatLockKey(concertId, seatNumber); //CONCERT-123-SEAT-A10 / memberId
		try {
			//setIfAbsent 는 성공적으로 Lock 이 설정되었는지 True or False 반환
			Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, memberId, 10, TimeUnit.MINUTES);
			if (lockAcquired == null || !lockAcquired) {
				throw new IllegalArgumentException("다른 유저가 해당 좌석을 예약중입니다.");
			}

			//좌석 확인
			List<Seat> seats = seatRepository.findAllByConcertIdAndSeatNumber(concertId, seatNumber);
			if (seats.size() != 1) {
				throw new IllegalArgumentException("좌석이 유효하지 않거나 중복된 데이터가 존재합니다.");
			}

			Seat seat = seats.get(0);

			if (seat.isBooked()) {
				throw new IllegalArgumentException("좌석이 이미 예약되었습니다.");
			}

			seat.book(memberId);
			seatRepository.save(seat);
		} catch (IllegalArgumentException e) {
			//lock 을 얻지 못했으면
			throw new IllegalStateException("좌석을 예약할 수 없습니다. 다른 유저가 이미 예약중입니다.");
		}
	}

	private void releaseLock(String lockKey) {
		redisTemplate.delete(lockKey);
	}
}
