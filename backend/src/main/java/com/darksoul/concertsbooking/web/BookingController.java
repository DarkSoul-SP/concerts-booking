package com.darksoul.concertsbooking.web;

import com.darksoul.concertsbooking.dto.BookingDto;
import com.darksoul.concertsbooking.service.BookingService;
import com.darksoul.concertsbooking.service.UserService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/my")
    public List<BookingDto> myBookings(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        var user = userService.getByUsername(principal.getUsername());
        return bookingService.findUserBookings(user).stream()
                .map(BookingDto::from)
                .toList();
    }

    @PostMapping("/{bookingId}/cancel")
    public BookingDto cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal
    ) {
        var user = userService.getByUsername(principal.getUsername());
        return BookingDto.from(bookingService.cancelBooking(user, bookingId));
    }
}
