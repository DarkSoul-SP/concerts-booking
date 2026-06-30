package com.darksoul.concertsbooking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false)
    private LocalDateTime concertDateTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ticketPrice;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int bookedSeats;

    @Getter(AccessLevel.NONE)
    @Version
    private long version;

    @Builder
    public Concert(String title, String artist, String venue, LocalDateTime concertDateTime, BigDecimal ticketPrice, int totalSeats) {
        this.title = title;
        this.artist = artist;
        this.venue = venue;
        this.concertDateTime = concertDateTime;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    public void bookSeats(int quantity) {
        if (quantity < 1 || quantity > getAvailableSeats()) {
            throw new IllegalArgumentException("Not enough seats available");
        }
        bookedSeats += quantity;
    }

    public void releaseSeats(int quantity) {
        bookedSeats = Math.max(0, bookedSeats - quantity);
    }
}
