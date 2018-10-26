package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;
import my.norxiva.myrrha.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Request {

    private RequestType type;

    // Urls
    private String baseUrl;
    private String callbackUrl;
    private String queryUrl;
    private String gatewayUrl;
    private String returnUrl;

    // Crypt
    private String merchantNo;
    private String username;
    private String password;
    private String privateKeyType;
    private String privateKey;
    private String privateKeyPassword;
    private String publicKeyType;
    private String publicKey;
    private String signatureAlgorithm;

    // Common
    private LocalDateTime createdTime = LocalDateTime.now();
    private TransactionType transactionType;
    private String content;
    private String encoding;

    public Request(RequestType type) {
        this.type = type;
    }
}
