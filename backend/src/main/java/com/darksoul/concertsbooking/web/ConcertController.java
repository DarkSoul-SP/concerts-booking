package com.darksoul.concertsbooking.web;

import com.darksoul.concertsbooking.dto.BookingDto;
import com.darksoul.concertsbooking.dto.BookingRequests.CreateBookingRequest;
import com.darksoul.concertsbooking.dto.ConcertDto;
import com.darksoul.concertsbooking.mapper.BookingMapper;
import com.darksoul.concertsbooking.mapper.ConcertMapper;
import com.darksoul.concertsbooking.service.BookingService;
import com.darksoul.concertsbooking.service.ConcertService;
import com.darksoul.concertsbooking.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;
    private final BookingService bookingService;
    private final UserService userService;
    private final ConcertMapper concertMapper;
    private final BookingMapper bookingMapper;

    @GetMapping
    public List<ConcertDto> listConcerts() {
        return concertService.findFutureConcerts().stream()
                .map(concertMapper::toDto)
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
        return bookingMapper.toDto(bookingService.createBooking(user, concertId, request.quantity()));
    }
}
