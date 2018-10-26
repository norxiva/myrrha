package my.norxiva.myrrha.channel.unionpay;

import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.ChannelType;
import my.norxiva.myrrha.channel.AbstractBaseTests;
import my.norxiva.myrrha.channel.bean.BatchTransactionRequest;
import my.norxiva.myrrha.channel.entity.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class UnionPayConverterTests extends AbstractBaseTests {
    
    private UnionPayConverter converter;

    @Before
    public void setUp() throws Exception {
        initParams(ChannelType.UNIONPAY);
        converter = new UnionPayConverter();
    }

    @Test
    public void testBatchTransaction() {
        BatchTransactionRequest request = new BatchTransactionRequest();
        BeanUtils.copyProperties(testRequest, request, "type");
        request.setOrderNo(UUID.randomUUID().toString());

        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setSerialNo(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        transaction.setBankCode("102");
        transaction.setAmount(BigDecimal.ONE);
        transaction.setBankAccountName("栾水");
        transaction.setBankAccountNo("666228480081223030");

        transactionList.add(transaction);

        request.setTransactions(transactionList);

        String content = converter.writeTo(request);
        log.info("content: {}", content);
    }
}
