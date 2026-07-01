package com.darksoul.concertsbooking;

import org.springframework.boot.SpringApplication;

public class TestConcertsBookingApplication {

  static void main(String[] args) {
    SpringApplication.from(ConcertsBookingApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
