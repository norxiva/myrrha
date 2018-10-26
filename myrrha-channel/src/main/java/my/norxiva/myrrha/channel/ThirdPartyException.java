package my.norxiva.myrrha.channel;

public class ThirdPartyException extends RuntimeException {

    public ThirdPartyException(String message) {
        super(message);
    }

    public ThirdPartyException(String message, Throwable cause) {
        super(message, cause);
    }
}
