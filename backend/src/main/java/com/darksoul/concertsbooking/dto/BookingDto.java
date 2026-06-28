package com.darksoul.concertsbooking.dto;

import com.darksoul.concertsbooking.domain.Booking;
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
) {

    public static BookingDto from(Booking booking) {
        var concert = booking.getConcert();
        return new BookingDto(
                booking.getId(),
                concert.getId(),
                concert.getTitle(),
                concert.getArtist(),
                concert.getVenue(),
                concert.getConcertDateTime(),
                booking.getQuantity(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getCancelledAt()
        );
    }
}
