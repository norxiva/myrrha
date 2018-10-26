package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;
import my.norxiva.myrrha.channel.entity.Transaction;

@Getter
@Setter
public class TransactionRequest extends AbstractTransactionRequest {
    private Transaction transaction;

    public TransactionRequest() {
        super(RequestType.TRANSACTION);
    }
}
