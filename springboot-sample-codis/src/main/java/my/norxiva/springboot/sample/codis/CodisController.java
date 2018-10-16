package my.norxiva.springboot.sample.codis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("codis")
public class CodisController {

    private StringRedisTemplate redisTemplate;

    public CodisController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping(value = "getSet", method = RequestMethod.POST)
    public String getSet(@RequestParam("key") String key, @RequestParam("value") String value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public String get(@RequestParam("key") String key) {
        return redisTemplate.opsForValue().get(key);
    }


}
