package com.sparta.domain.seat.repository;


import com.sparta.domain.seat.Seat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	@Query("SELECT s FROM Seat s WHERE s.concert.id = :concertId AND s.seatNumber = :seatNumber")
	List<Seat> findAllByConcertIdAndSeatNumber(@Param("concertId") Long concertId, @Param("seatNumber") String seatNumber);

	List<Seat> findByConcertId(Long concertId);
}
