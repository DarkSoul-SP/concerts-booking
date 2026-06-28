package com.darksoul.concertsbooking.dto;

import com.darksoul.concertsbooking.domain.Concert;
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
) {

    public static ConcertDto from(Concert concert) {
        return new ConcertDto(
                concert.getId(),
                concert.getTitle(),
                concert.getArtist(),
                concert.getVenue(),
                concert.getConcertDateTime(),
                concert.getTicketPrice(),
                concert.getTotalSeats(),
                concert.getBookedSeats(),
                concert.getAvailableSeats()
        );
    }
}
