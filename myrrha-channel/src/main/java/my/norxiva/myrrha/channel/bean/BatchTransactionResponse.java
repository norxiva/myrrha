package my.norxiva.myrrha.channel.bean;

import lombok.Getter;
import lombok.Setter;
import my.norxiva.myrrha.channel.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BatchTransactionResponse extends Response {

    private String orderNo;
    private List<Transaction> transactions = new ArrayList<>();
}
