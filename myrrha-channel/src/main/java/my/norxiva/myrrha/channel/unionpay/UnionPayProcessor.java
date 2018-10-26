package my.norxiva.myrrha.channel.unionpay;

import com.gnete.security.crypt.CryptException;
import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.channel.AbstractHttpProcessor;
import my.norxiva.myrrha.channel.ThirdPartyException;
import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.channel.bean.Response;
import my.norxiva.myrrha.util.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Slf4j
public class UnionPayProcessor extends AbstractHttpProcessor {
    private static final Header HEADER_XML
            = new BasicHeader("Content-Type", "application/xml; charset=gbk");


    private UnionPayConverter converter;
    private UnionPayCryptor cryptor;

    public UnionPayProcessor(HttpClient httpClient, UnionPayConverter converter, UnionPayCryptor cryptor) {
        super(httpClient);
        this.converter = converter;
        this.cryptor = cryptor;
    }

    @Override
    public <T extends Response> T execute(Request request) throws ThirdPartyException {
        try {
            String requestString = converter.writeTo(request);
            String signedRequestString = cryptor.sign(requestString, request);

            log.info("signed string: {}", signedRequestString);

            HttpRequestBase requestBase = compose(request, signedRequestString);

            HttpEntity responseEntity = doExecute(requestBase);
            String responseString = EntityUtils.toString(responseEntity, request.getEncoding());
            log.info("response string: {}", responseString);

            cryptor.verify(responseString, request);

            Response response = converter.readFrom(responseString, request.getType());

            //noinspection unchecked
            return (T) response;
        } catch (IOException ex) {
            log.error("Failed to read the HTTP entity from response!", ex);
            throw new ThirdPartyException("Error reading the HTTP entity from response", ex);
        }

    }

    private HttpRequestBase compose(Request request, String content) {
        HttpPost post = new HttpPost(request.getBaseUrl());
        post.setHeader(HEADER_XML);
        post.setEntity(new StringEntity(content, request.getEncoding()));

        return post;
    }
}
