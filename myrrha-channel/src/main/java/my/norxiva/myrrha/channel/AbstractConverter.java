package my.norxiva.myrrha.channel;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.channel.bean.*;

import java.util.Map;

@Slf4j
public class AbstractConverter implements Converter {

    protected static final FreeMarkerHelper FM_HELPER = new FreeMarkerHelper(
            "/templates/unionpay");

    protected String render(Template template, Map<String, Object> data) {
        return FM_HELPER.render(template, data);
    }

    @Override
    public String writeTo(Request request) {
        switch (request.getType()) {
            case TRANSACTION:
                return from((TransactionRequest) request);
            case BATCH_TRANSACTION:
                return from((BatchTransactionRequest) request);
            case TRANSACTION_QUERY:
                return from((TransactionQueryRequest) request);
            default:
                throw new UnsupportedOperationException("Unsupported request type was used!");
        }
    }

    @Override
    public Response readFrom(String response, RequestType type) {
        switch (type) {
            case TRANSACTION:
                return toTransactionResponse(response);
            case BATCH_TRANSACTION:
                return toBatchTransactionResponse(response);
            case TRANSACTION_QUERY:
                return toTransactionQueryResponse(response);
            default:
                throw new UnsupportedOperationException("Unsupported request type was used!");
        }
    }

    protected String from(BatchTransactionRequest request) {
        throw new UnsupportedOperationException("Batch transaction is not supported");
    }

    protected String from(TransactionRequest request) {
        throw new UnsupportedOperationException("Transaction is not supported");
    }

    protected String from(TransactionQueryRequest request) {
        throw new UnsupportedOperationException("Transaction query is not supported");
    }

    protected BatchTransactionResponse toBatchTransactionResponse(String content) {
        throw new UnsupportedOperationException("Batch transaction is not supported");
    }

    protected TransactionResponse toTransactionResponse(String content) {
        throw new UnsupportedOperationException("Transaction is not supported");
    }

    protected TransactionQueryResponse toTransactionQueryResponse(String content) {
        throw new UnsupportedOperationException("Transaction query is not supported");
    }




}
