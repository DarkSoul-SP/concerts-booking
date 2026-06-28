package com.darksoul.concertsbooking.repository;

import com.darksoul.concertsbooking.domain.Concert;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByConcertDateTimeAfterOrderByConcertDateTimeAsc(LocalDateTime dateTime);
}
