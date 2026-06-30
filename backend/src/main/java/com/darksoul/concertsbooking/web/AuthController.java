package com.darksoul.concertsbooking.web;

import com.darksoul.concertsbooking.dto.AuthRequests.LoginRequest;
import com.darksoul.concertsbooking.dto.AuthRequests.RegisterRequest;
import com.darksoul.concertsbooking.dto.UserDto;
import com.darksoul.concertsbooking.mapper.UserMapper;
import com.darksoul.concertsbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        userService.register(request.username(), request.password());
        return authenticate(request.username(), request.password(), servletRequest);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return authenticate(request.username(), request.password(), servletRequest);
    }

    @GetMapping("/me")
    public UserDto me(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return userMapper.toDto(userService.getByUsername(principal.getUsername()));
    }

    private UserDto authenticate(String username, String password, HttpServletRequest servletRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        servletRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return userMapper.toDto(userService.getByUsername(authentication.getName()));
    }
}
