package ruzicka.ets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EtsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtsApplication.class, args);
    }

}
