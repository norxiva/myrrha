package my.norxiva.springboot.sample.codis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("codis/cache")
public class CodisCacheController {

    private CodisCacheService codisCacheService;

    public CodisCacheController(CodisCacheService codisCacheService) {
        this.codisCacheService = codisCacheService;
    }

    @RequestMapping(value = "hi", method = RequestMethod.GET)
    public String hi(@RequestParam("name") String name) {
        return codisCacheService.hi(name);
    }

    @RequestMapping(value = "bye", method = RequestMethod.POST)
    public String bye(@RequestParam("name") String name) {
        return codisCacheService.bye(name);
    }

}
