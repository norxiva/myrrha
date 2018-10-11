package my.norxiva.springboot.sample.devtools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("sample")
public class SampleController {

    @RequestMapping("hi")
    public String hi() {
        return "Hello world!";
    }
}
