package com.darksoul.concertsbooking.repository;

import com.darksoul.concertsbooking.domain.Booking;
import com.darksoul.concertsbooking.domain.UserAccount;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"concert"})
    List<Booking> findByUserOrderByCreatedAtDesc(UserAccount user);
}
