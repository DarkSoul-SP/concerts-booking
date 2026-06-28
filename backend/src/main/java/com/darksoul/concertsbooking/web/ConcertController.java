package com.darksoul.concertsbooking.web;

import com.darksoul.concertsbooking.dto.BookingDto;
import com.darksoul.concertsbooking.dto.BookingRequests.CreateBookingRequest;
import com.darksoul.concertsbooking.dto.ConcertDto;
import com.darksoul.concertsbooking.service.BookingService;
import com.darksoul.concertsbooking.service.ConcertService;
import com.darksoul.concertsbooking.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concerts")
public class ConcertController {

    private final ConcertService concertService;
    private final BookingService bookingService;
    private final UserService userService;

    public ConcertController(ConcertService concertService, BookingService bookingService, UserService userService) {
        this.concertService = concertService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping
    public List<ConcertDto> listConcerts() {
        return concertService.findFutureConcerts().stream()
                .map(ConcertDto::from)
                .toList();
    }

    @PostMapping("/{concertId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto book(
            @PathVariable Long concertId,
            @Valid @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {
        var user = userService.getByUsername(principal.getUsername());
        return BookingDto.from(bookingService.createBooking(user, concertId, request.quantity()));
    }
}
