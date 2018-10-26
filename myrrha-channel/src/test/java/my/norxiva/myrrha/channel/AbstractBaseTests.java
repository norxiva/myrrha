package my.norxiva.myrrha.channel;

import my.norxiva.myrrha.ChannelType;
import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.util.StringUtils;
import my.norxiva.myrrha.util.json.JsonProviderHolder;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.Security;

public abstract class AbstractBaseTests {
    protected static final String DEFAULT_ENV = "dev";
    protected static final String ENV = System.getProperty("env", DEFAULT_ENV);
    private static final String BASE_DIRECTION = "data";
    private static final String SEPARATOR = System.getProperty("file.separator");

    protected TestRequest testRequest;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    protected void initParams(ChannelType channelType) throws Exception {
        String filename = BASE_DIRECTION + SEPARATOR + ENV + SEPARATOR + channelType.name() + ".json";
        String content = FileUtils.readFileToString(new File(ClassLoader.getSystemResource(filename).toURI()),
                Charset.forName(StringUtils.UTF8));
        testRequest = JsonProviderHolder.JACKSON.parse(content, TestRequest.class);
    }

    protected HttpClient httpClient() throws Exception {
        return HttpClients
                .custom()
                .setDefaultRequestConfig(RequestConfig
                        .custom()
                        .setConnectTimeout(30000)
                        .setConnectionRequestTimeout(30000)
                        .setSocketTimeout(30000)
                        .build())
                .setSSLContext(SSLContextBuilder
                        .create()
                        .setProtocol("TLS")
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .setSecureRandom(new SecureRandom())
                        .build())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }

    protected static String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }
}
