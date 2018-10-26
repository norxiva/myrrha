package my.norxiva.myrrha.channel;

import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.channel.bean.RequestType;
import my.norxiva.myrrha.channel.bean.Response;

public interface Converter {

    String writeTo(Request request);

    Response readFrom(String response, RequestType type);
}
