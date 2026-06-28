package com.darksoul.concertsbooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public final class BookingRequests {

    private BookingRequests() {
    }

    public record CreateBookingRequest(
            @Min(1) @Max(20) int quantity
    ) {
    }
}
