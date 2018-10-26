package my.norxiva.myrrha.channel;

import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.channel.bean.Response;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public abstract class AbstractHttpProcessor implements Processor {

    protected HttpClient httpClient;

    public AbstractHttpProcessor(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Map<String, String> generate(Request request) throws ThirdPartyException {
        throw new ThirdPartyException("Request generation is not expected by this processor");
    }

    @Override
    public <T extends Response> T handle(String notification, Function<String, Optional<String>> function)
            throws ThirdPartyException {
        throw new ThirdPartyException("Notification is not expected by this processor");
    }

    protected HttpEntity doExecute(HttpRequestBase request) {
        try {
            return httpClient.execute(request).getEntity();
        } catch (IOException err) {
            log.error("Failed to communicate with remote server due to an exception!", err);
            throw new ThirdPartyException("Error connecting to remote server", err);
        }
    }


}
