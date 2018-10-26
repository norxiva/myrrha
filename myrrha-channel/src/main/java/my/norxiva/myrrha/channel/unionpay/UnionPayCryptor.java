package my.norxiva.myrrha.channel.unionpay;

import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.channel.ThirdPartyException;
import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.util.SecurityUtils;
import my.norxiva.myrrha.util.StringUtils;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UnionPayCryptor {

    private static final String COMPOSE_CONTENT = "<SIGNED_MSG></SIGNED_MSG>";
    private static final String COMPOSE_PATTERN = "<SIGNED_MSG>(.*)</SIGNED_MSG>";
    private static final String COMPOSE_FORMAT = "<SIGNED_MSG>%s</SIGNED_MSG>";

    public String sign(String content, Request request) {
//        Crypt crypt = new Crypt("gbk");
//        String beforeSignContent = origin.replace("<SIGNED_MSG></SIGNED_MSG>", StringUtils.EMPTY);
//        return crypt.sign(beforeSignContent, "C:/config/ORA@TEST1.p12", "123456");

        PrivateKey privateKey = SecurityUtils.from(request.getPrivateKeyType(),
                request.getPrivateKey(), request.getPrivateKeyPassword());
        String composed = content.replace(COMPOSE_CONTENT, StringUtils.EMPTY);
        String signature = Hex.encodeHexString(SecurityUtils.sign(request.getSignatureAlgorithm(),
                privateKey, composed.getBytes(Charset.forName(request.getEncoding()))));
        return content.replace(COMPOSE_CONTENT, String.format(COMPOSE_FORMAT, signature));
    }

    public void verify(String content, Request request) {

        PublicKey publicKey = SecurityUtils.from(request.getPublicKeyType(), request.getPublicKey());
        String composed = content.replaceAll(COMPOSE_PATTERN, StringUtils.EMPTY);
        Matcher matcher = Pattern.compile(COMPOSE_PATTERN).matcher(content);
        if (!matcher.find()) {
            throw new ThirdPartyException("Signature was not found");
        }
        String signature = matcher.group(1);
        if (SecurityUtils.verify(request.getSignatureAlgorithm(), publicKey,
                composed.getBytes(Charset.forName(request.getEncoding())),
                signature.getBytes(Charset.forName(request.getEncoding())))) {
            log.error("The invalid signature '{}' was found in response '{}'!", signature, content);
            throw new ThirdPartyException("Invalid signature found in response");
        }

    }

    public static void main(String[] args) {
        String s = "<SIGNED_MSG>123</SIGNED_MSG>";
        System.out.println(s.replaceAll(COMPOSE_PATTERN, StringUtils.EMPTY));
        // 把要匹配的字符串写成正则表达式，然后要提取的字符使用括号括起来
        // 在这里，我们要提取最后一个数字，正则规则就是“一个数字加上大于等于0个非数字再加上结束符”
        Pattern pattern = Pattern.compile(COMPOSE_PATTERN);
        Matcher matcher = pattern.matcher(s);
        if(matcher.find())
            System.out.println(matcher.group(1));
    }
}
