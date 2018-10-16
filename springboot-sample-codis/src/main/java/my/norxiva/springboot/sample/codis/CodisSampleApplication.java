package my.norxiva.springboot.sample.codis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CodisSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodisSampleApplication.class, args);
    }
}
