package com.darksoul.concertsbooking.service;

import com.darksoul.concertsbooking.repository.ConcertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock
    ConcertRepository concertRepository;

    @InjectMocks
    ConcertService concertService;

    @Test
    void findFutureConcerts_delegatesToRepositoryWithCurrentTime() {
        when(concertRepository.findByConcertDateTimeAfterOrderByConcertDateTimeAsc(any())).thenReturn(List.of());

        var result = concertService.findFutureConcerts();

        assertThat(result).isEmpty();
        verify(concertRepository).findByConcertDateTimeAfterOrderByConcertDateTimeAsc(any());
    }
}
