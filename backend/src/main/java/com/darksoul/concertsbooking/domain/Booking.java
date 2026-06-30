package com.darksoul.concertsbooking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Concert concert;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime cancelledAt;

    public Booking(UserAccount user, Concert concert, int quantity) {
        this.user = user;
        this.concert = concert;
        this.quantity = quantity;
        this.totalPrice = concert.getTicketPrice().multiply(BigDecimal.valueOf(quantity));
        this.createdAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    public void cancel() {
        status = BookingStatus.CANCELLED;
        cancelledAt = LocalDateTime.now();
    }
}
