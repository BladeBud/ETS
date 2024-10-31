package ruzicka.ets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EtsApplication {

    private static final Logger log = LoggerFactory.getLogger(EtsApplication.class);

    public static void main(String[] args) {
        log.info("Starting EtsApplication...");
        SpringApplication.run(EtsApplication.class, args);
        log.info("EtsApplication started successfully.");
    }
}