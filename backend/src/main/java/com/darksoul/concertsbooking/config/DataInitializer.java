package com.darksoul.concertsbooking.config;

import com.darksoul.concertsbooking.domain.Concert;
import com.darksoul.concertsbooking.domain.UserAccount;
import com.darksoul.concertsbooking.repository.ConcertRepository;
import com.darksoul.concertsbooking.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            ConcertRepository concertRepository,
            UserAccountRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(UserAccount.builder()
                        .username("demo")
                        .passwordHash(passwordEncoder.encode("password"))
                        .build());
            }

            if (concertRepository.count() == 0) {
                concertRepository.saveAll(List.of(
                        Concert.builder()
                                .title("Neon Skyline Tour").artist("Ava North").venue("Riverfront Arena")
                                .concertDateTime(scheduledAt(8, 20, 0))
                                .ticketPrice(new BigDecimal("69.00")).totalSeats(180)
                                .build(),
                        Concert.builder()
                                .title("City Lights Live").artist("The Night Signals").venue("Grand Hall")
                                .concertDateTime(scheduledAt(15, 19, 30))
                                .ticketPrice(new BigDecimal("54.50")).totalSeats(220)
                                .build(),
                        Concert.builder()
                                .title("Acoustic Evening").artist("Marta Lane").venue("Oak Theatre")
                                .concertDateTime(scheduledAt(22, 18, 45))
                                .ticketPrice(new BigDecimal("38.00")).totalSeats(90)
                                .build(),
                        Concert.builder()
                                .title("Electric Pulse").artist("Voltage Room").venue("Metro Dome")
                                .concertDateTime(scheduledAt(35, 21, 0))
                                .ticketPrice(new BigDecimal("82.75")).totalSeats(350)
                                .build(),
                        Concert.builder()
                                .title("Piano After Dark").artist("Leo Vance").venue("Blue Note Stage")
                                .concertDateTime(scheduledAt(48, 20, 15))
                                .ticketPrice(new BigDecimal("46.00")).totalSeats(120)
                                .build()
                ));
            }
        };
    }

    private static LocalDateTime scheduledAt(int daysFromNow, int hour, int minute) {
        return LocalDateTime.now()
                .plusDays(daysFromNow)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
    }
}
