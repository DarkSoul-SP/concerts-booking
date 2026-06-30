package com.darksoul.concertsbooking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConcertDto(
        Long id,
        String title,
        String artist,
        String venue,
        LocalDateTime concertDateTime,
        BigDecimal ticketPrice,
        int totalSeats,
        int bookedSeats,
        int availableSeats
) {}
