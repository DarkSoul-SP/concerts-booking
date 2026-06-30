package com.darksoul.concertsbooking.mapper;

import com.darksoul.concertsbooking.domain.Booking;
import com.darksoul.concertsbooking.dto.BookingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "concert.id", target = "concertId")
    @Mapping(source = "concert.title", target = "concertTitle")
    @Mapping(source = "concert.artist", target = "artist")
    @Mapping(source = "concert.venue", target = "venue")
    @Mapping(source = "concert.concertDateTime", target = "concertDateTime")
    BookingDto toDto(Booking booking);
}
