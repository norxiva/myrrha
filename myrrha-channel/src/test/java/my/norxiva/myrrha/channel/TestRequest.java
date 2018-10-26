package my.norxiva.myrrha.channel;

import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.channel.bean.RequestType;

public class TestRequest extends Request {
    public TestRequest() {
        super(RequestType.TRANSACTION);
    }
}
