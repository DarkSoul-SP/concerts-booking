package com.darksoul.concertsbooking.mapper;

import com.darksoul.concertsbooking.domain.Concert;
import com.darksoul.concertsbooking.dto.ConcertDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConcertMapper {
    ConcertDto toDto(Concert concert);
}
