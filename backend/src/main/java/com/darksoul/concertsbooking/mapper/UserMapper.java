package com.darksoul.concertsbooking.mapper;

import com.darksoul.concertsbooking.domain.UserAccount;
import com.darksoul.concertsbooking.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserAccount user);
}
