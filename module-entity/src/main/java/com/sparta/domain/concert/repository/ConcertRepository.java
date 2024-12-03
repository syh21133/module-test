package com.sparta.domain.concert.repository;


import com.sparta.domain.concert.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    Page<Concert> findByConcertNameContaining(String concertName, Pageable pageable);
}
