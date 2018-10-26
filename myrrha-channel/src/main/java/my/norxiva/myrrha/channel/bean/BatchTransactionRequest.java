package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;
import my.norxiva.myrrha.channel.entity.Transaction;

import java.util.List;

@Getter
@Setter
public class BatchTransactionRequest extends AbstractTransactionRequest {

    private List<Transaction> transactions;

    public BatchTransactionRequest() {
        super(RequestType.BATCH_TRANSACTION);
    }
}
