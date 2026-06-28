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
                userRepository.save(new UserAccount("demo", passwordEncoder.encode("password")));
            }

            if (concertRepository.count() == 0) {
                concertRepository.saveAll(List.of(
                        new Concert("Neon Skyline Tour", "Ava North", "Riverfront Arena",
                                scheduledAt(8, 20, 0),
                                new BigDecimal("69.00"), 180),
                        new Concert("City Lights Live", "The Night Signals", "Grand Hall",
                                scheduledAt(15, 19, 30),
                                new BigDecimal("54.50"), 220),
                        new Concert("Acoustic Evening", "Marta Lane", "Oak Theatre",
                                scheduledAt(22, 18, 45),
                                new BigDecimal("38.00"), 90),
                        new Concert("Electric Pulse", "Voltage Room", "Metro Dome",
                                scheduledAt(35, 21, 0),
                                new BigDecimal("82.75"), 350),
                        new Concert("Piano After Dark", "Leo Vance", "Blue Note Stage",
                                scheduledAt(48, 20, 15),
                                new BigDecimal("46.00"), 120)
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
