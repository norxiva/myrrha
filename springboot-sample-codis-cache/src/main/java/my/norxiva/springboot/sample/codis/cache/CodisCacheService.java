package my.norxiva.springboot.sample.codis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CodisCacheService {

    @Cacheable(value = CacheKeyConstant.CACHE_KEY_HI, key = "#name")
    public String hi(String name) {
        log.info("say hi, {}", name);
        return String.format("Hi, %s", name);
    }

    @CacheEvict(value = CacheKeyConstant.CACHE_KEY_HI, key = "#name")
    public String bye(String name) {
        log.info("say bye, {}", name);
        return String.format("Bye, %s", name);
    }


}
