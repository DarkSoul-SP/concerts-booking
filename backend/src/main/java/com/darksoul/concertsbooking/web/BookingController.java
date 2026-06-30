package com.darksoul.concertsbooking.web;

import com.darksoul.concertsbooking.dto.BookingDto;
import com.darksoul.concertsbooking.mapper.BookingMapper;
import com.darksoul.concertsbooking.service.BookingService;
import com.darksoul.concertsbooking.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final BookingMapper bookingMapper;

    @GetMapping("/my")
    public List<BookingDto> myBookings(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        var user = userService.getByUsername(principal.getUsername());
        return bookingService.findUserBookings(user).stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingDto cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {
        var user = userService.getByUsername(principal.getUsername());
        return bookingMapper.toDto(bookingService.cancelBooking(user, bookingId));
    }
}
