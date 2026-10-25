package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfig {

    /** Se inyecta un reloj para poder fijar la fecha en las pruebas. */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
