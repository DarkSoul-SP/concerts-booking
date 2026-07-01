package com.darksoul.concertsbooking.web;

import com.darksoul.concertsbooking.AbstractIntegrationTest;
import com.darksoul.concertsbooking.domain.Concert;
import com.darksoul.concertsbooking.repository.BookingRepository;
import com.darksoul.concertsbooking.repository.ConcertRepository;
import com.darksoul.concertsbooking.repository.UserAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserAccountRepository userRepository;

    Concert concert;
    MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        concert = concertRepository.save(Concert.builder()
                .title("Live Show").artist("The Band").venue("Big Arena")
                .concertDateTime(LocalDateTime.now().plusDays(30))
                .ticketPrice(new BigDecimal("60.00"))
                .totalSeats(100)
                .build());

        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession();
    }

    @AfterEach
    void cleanup() {
        bookingRepository.deleteAll();
        concertRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void listConcerts_returnsSeededConcert() throws Exception {
        mockMvc.perform(get("/api/concerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Live Show"))
                .andExpect(jsonPath("$[0].availableSeats").value(100));
    }

    @Test
    void createBooking_happyPath_returnsBookingAndReducesSeats() throws Exception {
        mockMvc.perform(post("/api/concerts/" + concert.getId() + "/bookings")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.totalPrice").value(180.0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/api/concerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availableSeats").value(97));
    }

    @Test
    void myBookings_returnsUserBookings() throws Exception {
        mockMvc.perform(post("/api/concerts/" + concert.getId() + "/bookings")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":2}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/bookings/my").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].concertTitle").value("Live Show"))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    void cancelBooking_setsStatusCancelledAndReleasesSeats() throws Exception {
        var bookResult = mockMvc.perform(post("/api/concerts/" + concert.getId() + "/bookings")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":5}"))
                .andExpect(status().isCreated())
                .andReturn();

        long bookingId = objectMapper.readTree(bookResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/bookings/" + bookingId + "/cancel").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.cancelledAt").isNotEmpty());

        mockMvc.perform(get("/api/concerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availableSeats").value(100));
    }

    @Test
    void createBooking_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/concerts/" + concert.getId() + "/bookings")
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createBooking_invalidQuantityZero_returns400() throws Exception {
        mockMvc.perform(post("/api/concerts/" + concert.getId() + "/bookings")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_notEnoughSeats_returns400() throws Exception {
        Concert smallConcert = concertRepository.save(Concert.builder()
                .title("Intimate Gig").artist("Solo Artist").venue("Small Club")
                .concertDateTime(LocalDateTime.now().plusDays(14))
                .ticketPrice(new BigDecimal("30.00"))
                .totalSeats(2)
                .build());

        mockMvc.perform(post("/api/concerts/" + smallConcert.getId() + "/bookings")
                        .session(session)
                        .contentType(APPLICATION_JSON)
                        .content("{\"quantity\":3}"))
                .andExpect(status().isBadRequest());
    }
}
