package com.darksoul.concertsbooking.dto;

import com.darksoul.concertsbooking.domain.UserAccount;

public record UserDto(Long id, String username) {

    public static UserDto from(UserAccount user) {
        return new UserDto(user.getId(), user.getUsername());
    }
}
