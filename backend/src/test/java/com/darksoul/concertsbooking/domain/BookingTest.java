package com.darksoul.concertsbooking.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    private Concert buildConcert(BigDecimal price, int seats) {
        return Concert.builder()
                .title("Show").artist("Artist").venue("Venue")
                .concertDateTime(LocalDateTime.now().plusDays(7))
                .ticketPrice(price)
                .totalSeats(seats)
                .build();
    }

    private UserAccount buildUser() {
        return UserAccount.builder().username("alice").passwordHash("hash").build();
    }

    @Test
    void constructor_computesTotalPrice() {
        Concert concert = buildConcert(new BigDecimal("25.50"), 100);
        Booking booking = new Booking(buildUser(), concert, 4);

        assertThat(booking.getTotalPrice()).isEqualByComparingTo("102.00");
        assertThat(booking.getQuantity()).isEqualTo(4);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(booking.getCreatedAt()).isNotNull();
        assertThat(booking.getCancelledAt()).isNull();
    }

    @Test
    void cancel_setsStatusAndTimestamp() {
        Concert concert = buildConcert(new BigDecimal("10.00"), 50);
        Booking booking = new Booking(buildUser(), concert, 2);

        booking.cancel();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(booking.getCancelledAt()).isNotNull();
        assertThat(booking.isActive()).isFalse();
    }
}
