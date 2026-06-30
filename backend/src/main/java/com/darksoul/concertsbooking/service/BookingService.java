package com.darksoul.concertsbooking.service;

import com.darksoul.concertsbooking.domain.Booking;
import com.darksoul.concertsbooking.domain.BookingStatus;
import com.darksoul.concertsbooking.domain.UserAccount;
import com.darksoul.concertsbooking.repository.BookingRepository;
import com.darksoul.concertsbooking.repository.ConcertRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;

    @Transactional
    public Booking createBooking(UserAccount user, Long concertId, int quantity) {
        var concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concert was not found"));

        if (!concert.getConcertDateTime().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only future concerts can be booked");
        }

        if (quantity < 1 || quantity > concert.getAvailableSeats()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough seats available");
        }

        concert.bookSeats(quantity);
        return bookingRepository.save(new Booking(user, concert, quantity));
    }

    @Transactional(readOnly = true)
    public List<Booking> findUserBookings(UserAccount user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Booking cancelBooking(UserAccount user, Long bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking was not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking belongs to another user");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }

        booking.cancel();
        booking.getConcert().releaseSeats(booking.getQuantity());
        return booking;
    }
}
