package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionQueryRequest extends Request {

    private String orderNo;
    private String serialNo;

    public TransactionQueryRequest() {
        super(RequestType.TRANSACTION_QUERY);
    }
}
