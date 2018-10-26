package my.norxiva.myrrha.channel.unionpay;

import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.ChannelType;
import my.norxiva.myrrha.channel.AbstractBaseTests;
import my.norxiva.myrrha.channel.bean.BatchTransactionRequest;
import my.norxiva.myrrha.channel.bean.Response;
import my.norxiva.myrrha.channel.entity.Transaction;
import my.norxiva.myrrha.channel.unionpay.http.HttpClientUtils;
import my.norxiva.myrrha.util.json.JsonProviderHolder;
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
public class UnionPayProcessorTests extends AbstractBaseTests {

    private UnionPayProcessor processor;

    @Before
    public void setUp() throws Exception {
        initParams(ChannelType.UNIONPAY);
        processor = new UnionPayProcessor(HttpClientUtils.buildHttpClient(),
                new UnionPayConverter(), new UnionPayCryptor());
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

        Response response = processor.execute(request);

        log.info("response: {}", JsonProviderHolder.JACKSON.convertObj(response));

    }
}
