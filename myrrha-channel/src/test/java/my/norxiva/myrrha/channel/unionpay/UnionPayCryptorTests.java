package my.norxiva.myrrha.channel.unionpay;

import lombok.extern.slf4j.Slf4j;
import my.norxiva.myrrha.ChannelType;
import my.norxiva.myrrha.channel.AbstractBaseTests;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class UnionPayCryptorTests extends AbstractBaseTests {

    private UnionPayCryptor cryptor;

    @Before
    public void setUp() throws Exception {
        initParams(ChannelType.UNIONPAY);
        cryptor = new UnionPayCryptor();
    }

    @Test
    public void testSign() {
        String content = "123456<SIGNED_MSG></SIGNED_MSG>";
        String sign = cryptor.sign(content, testRequest);
        log.info("sign: {}", sign);
    }

    @Test
    public void testVerify() {
        String content = "<?xml version=\"1.0\" encoding=\"GBK\"?><GZELINK><INFO><TRX_CODE>100001</TRX_CODE><VERSION>05</VERSION><DATA_TYPE></DATA_TYPE><REQ_SN>4f99a8e6-f58c-4ad3-920f-a52f10250dd4</REQ_SN><RET_CODE>0000</RET_CODE><ERR_MSG>系统接收成功</ERR_MSG><SIGNED_MSG>111c0dfe15f2e7381b85185fe3e994b18898e63712077d65923eb0dfdfb5c36500e3542083bb445cb6deec463a9578572fc2f0e915e5eee69be0b23c45a2df1850bc403141a254d756f1a11b3c6695b4fd39b42924ac435d23af14557fc67221621c48fb8fbe87ac8c70184e73329802416a7d5c071816ddec5cc9f736b0a885</SIGNED_MSG></INFO><BODY><RET_DETAILS><RET_DETAIL><SN>20181026111439410</SN><RET_CODE>0000</RET_CODE></RET_DETAIL></RET_DETAILS></BODY></GZELINK>";
        cryptor.verify(content, testRequest);
    }
}
