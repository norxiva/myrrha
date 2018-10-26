package my.norxiva.myrrha.channel.unionpay;

import com.google.common.collect.ImmutableList;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.channel.AbstractConverter;
import my.norxiva.myrrha.channel.ThirdPartyException;
import my.norxiva.myrrha.channel.bean.BatchTransactionRequest;
import my.norxiva.myrrha.channel.bean.BatchTransactionResponse;
import my.norxiva.myrrha.channel.entity.Transaction;
import my.norxiva.myrrha.channel.unionpay.bean.BatchTransactionMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UnionPayConverter extends AbstractConverter {

    private static final String TEMPLATE_ATTRIBUTE_REQUEST = "request";
    private static final String TEMPLATE_ATTRIBUTE_SUM = "sum";
    private static final String TEMPLATE_ATTRIBUTE_COUNT = "count";

    private static final List<String> BATCH_TRANSACTION_SUCCESS_CODES = ImmutableList.of("0000");

    private final Template batchTransactionTemplate;

    private final XStream batchTransactionXStream;

    public UnionPayConverter() {
        Configuration configuration = FM_HELPER.getConfiguration();

        try {
            batchTransactionTemplate = configuration.getTemplate("UNIONPAY_BatchTransaction.ftl");
        } catch (IOException ex) {
            throw new ThirdPartyException("Failed to initialize templates.", ex);
        }

        batchTransactionXStream = new XStream();
        batchTransactionXStream.processAnnotations(BatchTransactionMapper.class);
        batchTransactionXStream.ignoreUnknownElements();

    }

    @Override
    protected String from(BatchTransactionRequest request) {
        Map<String, Object> data = new HashMap<>();
        data.put(TEMPLATE_ATTRIBUTE_REQUEST, request);
        BigDecimal sum = BigDecimal.valueOf(request.getTransactions()
                .stream()
                .mapToDouble(it ->
                        it.getAmount().doubleValue())
                .sum());
        long count = request.getTransactions().size();
        data.put(TEMPLATE_ATTRIBUTE_SUM, sum);
        data.put(TEMPLATE_ATTRIBUTE_COUNT, count);
        return render(batchTransactionTemplate, data);
    }

    @Override
    protected BatchTransactionResponse toBatchTransactionResponse(String content) {
        BatchTransactionResponse response = new BatchTransactionResponse();
        BatchTransactionMapper mapper = (BatchTransactionMapper) batchTransactionXStream.fromXML(content);
        response.setCode(mapper.getInfo().getReturnCode());
        response.setMessage(mapper.getInfo().getErrorMsg());

        if (BATCH_TRANSACTION_SUCCESS_CODES.contains(response.getCode())) {
            response.setOrderNo(mapper.getInfo().getRequestSerialNo());

            mapper.getBody().getDetails().forEach(it -> {
                Transaction transaction = new Transaction();
                transaction.setSerialNo(it.getSerialNo());
                transaction.setCode(it.getReturnCode());
                transaction.setMessage(it.getErrorMsg());
                response.getTransactions().add(transaction);
            });
        }


        return response;
    }
}
