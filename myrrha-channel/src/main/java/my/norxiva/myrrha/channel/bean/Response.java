package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Response {

    private String code;
    private String message;
    private String content;
    private final LocalDateTime createdTime = LocalDateTime.now();
}
