package com.darksoul.concertsbooking.dto;

import com.darksoul.concertsbooking.domain.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingDto(
        Long id,
        Long concertId,
        String concertTitle,
        String artist,
        String venue,
        LocalDateTime concertDateTime,
        int quantity,
        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt,
        LocalDateTime cancelledAt
) {}
