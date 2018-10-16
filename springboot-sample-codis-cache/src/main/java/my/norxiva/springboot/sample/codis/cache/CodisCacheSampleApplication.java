package my.norxiva.springboot.sample.codis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CodisCacheSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodisCacheSampleApplication.class, args);
    }
}
