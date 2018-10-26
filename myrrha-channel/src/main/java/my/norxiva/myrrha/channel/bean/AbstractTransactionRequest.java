package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractTransactionRequest extends Request {

    private String orderNo;

    public AbstractTransactionRequest(RequestType type) {
        super(type);
    }
}
