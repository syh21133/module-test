package com.sparta.domain.ticket.repository;


import com.sparta.domain.ticket.Ticket;
import com.sparta.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketRepositoryCustom {
    Page<Ticket> findByMemberId(UserDetailsImpl authMember, Pageable pageable);
}
