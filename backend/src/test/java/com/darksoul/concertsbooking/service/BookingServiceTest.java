package com.darksoul.concertsbooking.service;

import com.darksoul.concertsbooking.domain.Booking;
import com.darksoul.concertsbooking.domain.BookingStatus;
import com.darksoul.concertsbooking.domain.Concert;
import com.darksoul.concertsbooking.domain.UserAccount;
import com.darksoul.concertsbooking.repository.BookingRepository;
import com.darksoul.concertsbooking.repository.ConcertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ConcertRepository concertRepository;

    @InjectMocks
    BookingService bookingService;

    UserAccount user;
    UserAccount otherUser;
    Concert concert;

    @BeforeEach
    void setUp() {
        user = UserAccount.builder().username("alice").passwordHash("hash").build();
        ReflectionTestUtils.setField(user, "id", 1L);

        otherUser = UserAccount.builder().username("bob").passwordHash("hash").build();
        ReflectionTestUtils.setField(otherUser, "id", 2L);

        concert = Concert.builder()
                .title("Test Concert").artist("Test Artist").venue("Arena")
                .concertDateTime(LocalDateTime.now().plusDays(10))
                .ticketPrice(new BigDecimal("50.00"))
                .totalSeats(100)
                .build();
    }

    // --- createBooking ---

    @Test
    void createBooking_happyPath_decrementsSeatsAndSavesBooking() {
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking result = bookingService.createBooking(user, 1L, 3);

        assertThat(concert.getBookedSeats()).isEqualTo(3);
        assertThat(result.getQuantity()).isEqualTo(3);
        assertThat(result.getTotalPrice()).isEqualByComparingTo("150.00");
        assertThat(result.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_concertNotFound_throws404() {
        when(concertRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(user, 99L, 1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Concert was not found");
    }

    @Test
    void createBooking_pastConcert_throws400() {
        Concert past = Concert.builder()
                .title("Old Show").artist("A").venue("B")
                .concertDateTime(LocalDateTime.now().minusDays(1))
                .ticketPrice(new BigDecimal("10.00"))
                .totalSeats(50)
                .build();
        when(concertRepository.findById(1L)).thenReturn(Optional.of(past));

        assertThatThrownBy(() -> bookingService.createBooking(user, 1L, 1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only future concerts can be booked");
    }

    @Test
    void createBooking_quantityExceedsAvailableSeats_throws400() {
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));

        assertThatThrownBy(() -> bookingService.createBooking(user, 1L, 101))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not enough seats available");
    }

    @Test
    void createBooking_quantityZero_throws400() {
        when(concertRepository.findById(1L)).thenReturn(Optional.of(concert));

        assertThatThrownBy(() -> bookingService.createBooking(user, 1L, 0))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not enough seats available");
    }

    // --- cancelBooking ---

    @Test
    void cancelBooking_happyPath_cancelsAndReleasesSeats() {
        concert.bookSeats(3);
        Booking booking = new Booking(user, concert, 3);
        ReflectionTestUtils.setField(booking, "id", 10L);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.cancelBooking(user, 10L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(result.getCancelledAt()).isNotNull();
        assertThat(concert.getBookedSeats()).isEqualTo(0);
    }

    @Test
    void cancelBooking_notFound_throws404() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.cancelBooking(user, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Booking was not found");
    }

    @Test
    void cancelBooking_anotherUsersBooking_throws403() {
        Booking booking = new Booking(otherUser, concert, 2);
        ReflectionTestUtils.setField(booking, "id", 10L);
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(user, 10L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Booking belongs to another user");
    }

    @Test
    void cancelBooking_alreadyCancelled_idempotentNoSeatRelease() {
        concert.bookSeats(2);
        Booking booking = new Booking(user, concert, 2);
        ReflectionTestUtils.setField(booking, "id", 10L);
        booking.cancel();
        concert.releaseSeats(2);
        int seatsBeforeCancel = concert.getBookedSeats();
        when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.cancelBooking(user, 10L);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(concert.getBookedSeats()).isEqualTo(seatsBeforeCancel);
        verify(bookingRepository, never()).save(any());
    }
}
