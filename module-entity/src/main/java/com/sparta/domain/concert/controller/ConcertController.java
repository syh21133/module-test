package com.sparta.domain.concert.controller;


import com.sparta.domain.concert.dto.ConcertRequest;
import com.sparta.domain.concert.dto.ConcertResponse;
import com.sparta.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.domain.concert.service.ConcertService;
import com.sparta.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping
    public ResponseEntity<ConcertResponse> registerConcert(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ConcertRequest request
    ) {
        ConcertResponse response = concertService.registerConcert(userDetails, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> updateConcert(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long concertId,
            @RequestBody ConcertUpdateRequest request
    ) {
        ConcertResponse response = concertService.updateConcert(userDetails, concertId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ConcertResponse>> getConcerts(
            @RequestParam(value = "name", required = false) String name, // 검색 조건
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ConcertResponse> concertResponses = concertService.getConcerts(name, page, size);
        return ResponseEntity.ok(concertResponses);
    }
    // 공연 단건 조회
    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> getConcert(
            @PathVariable Long concertId
    ) {
        ConcertResponse concertResponse = concertService.getConcertById(concertId);
        return ResponseEntity.ok(concertResponse);
    }

}
