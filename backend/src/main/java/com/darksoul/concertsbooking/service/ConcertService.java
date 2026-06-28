package com.darksoul.concertsbooking.service;

import com.darksoul.concertsbooking.domain.Concert;
import com.darksoul.concertsbooking.repository.ConcertRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertService(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }

    public List<Concert> findFutureConcerts() {
        return concertRepository.findByConcertDateTimeAfterOrderByConcertDateTimeAsc(LocalDateTime.now());
    }
}
