package my.norxiva.myrrha.channel.unionpay.http;

import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.channel.ThirdPartyException;
import my.norxiva.myrrha.util.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Slf4j
public class HttpClientUtils {

    public static SSLContext buildSSLContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }}, null);
            return context;
        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            log.error(ex.getMessage(), ex);
            throw new ThirdPartyException("SSL Context build error", ex);
        }
    }

//    public static X509HostnameVerifier buildX509HostnameVerifier() {
//        return new X509HostnameVerifier() {
//            @Override
//            public void verify(String host, SSLSocket ssl) {
//
//            }
//
//            @Override
//            public void verify(String host, X509Certificate cert) {
//
//            }
//
//            @Override
//            public void verify(String host, String[] cns, String[] subjectAlts) {
//
//            }
//
//            @Override
//            public boolean verify(String s, SSLSession sslSession) {
//                return true;
//            }
//        };
//    }

//    @SuppressWarnings("deprecation")
//    public static HttpClient buildHttpClient() {
//        SchemeRegistry registry = new SchemeRegistry();
//        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
//        registry.register(new Scheme("https", 443,
//                new SSLSocketFactory(buildSSLContext(), buildX509HostnameVerifier())));
//
//        PoolingClientConnectionManager manager = new PoolingClientConnectionManager(registry);
////        manager.setMaxTotal(200);
////        manager.setDefaultMaxPerRoute(20);
//        manager.setMaxPerRoute(new HttpRoute(new HttpHost("localhost", 80)), 50);
//        HttpClient httpClient = new DefaultHttpClient(manager);
////        HttpClient httpClient = new DefaultHttpClient();
//        httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, StringUtils.GBK);
//        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
//        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
//
//        return httpClient;
//    }

    public static HttpClient buildHttpClient() {
//        SSLContext context = SSLContextBuilder
//                .create()
//                .setProtocol("TLS")
//                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
//                .setSecureRandom(new SecureRandom()).build();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(
                        buildSSLContext(), new NoopHostnameVerifier()))
//                        context, new NoopHostnameVerifier()))
                .build();

        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
        manager.setMaxPerRoute(new HttpRoute(new HttpHost("localhost", 80)), 50);
        manager.setMaxTotal(200);
        manager.setDefaultMaxPerRoute(20);

        return HttpClientBuilder
                .create()
//                .setSSLContext(buildSSLContext())
                .setConnectionManager(manager)
                .build();
    }
}
