package com.darksoul.concertsbooking.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcertTest {

    private Concert build(int totalSeats) {
        return Concert.builder()
                .title("Test").artist("Artist").venue("Venue")
                .concertDateTime(LocalDateTime.now().plusDays(5))
                .ticketPrice(new BigDecimal("40.00"))
                .totalSeats(totalSeats)
                .build();
    }

    @Test
    void bookSeats_reducesAvailableSeats() {
        Concert concert = build(50);
        concert.bookSeats(10);
        assertThat(concert.getBookedSeats()).isEqualTo(10);
        assertThat(concert.getAvailableSeats()).isEqualTo(40);
    }

    @Test
    void bookSeats_exactCapacity_succeeds() {
        Concert concert = build(10);
        concert.bookSeats(10);
        assertThat(concert.getAvailableSeats()).isEqualTo(0);
    }

    @Test
    void bookSeats_overCapacity_throws() {
        Concert concert = build(5);
        assertThatThrownBy(() -> concert.bookSeats(6))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bookSeats_zeroQuantity_throws() {
        Concert concert = build(10);
        assertThatThrownBy(() -> concert.bookSeats(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void releaseSeats_restoresAvailability() {
        Concert concert = build(20);
        concert.bookSeats(5);
        concert.releaseSeats(5);
        assertThat(concert.getAvailableSeats()).isEqualTo(20);
    }

    @Test
    void releaseSeats_neverGoesNegative() {
        Concert concert = build(10);
        concert.releaseSeats(999);
        assertThat(concert.getBookedSeats()).isEqualTo(0);
    }
}
