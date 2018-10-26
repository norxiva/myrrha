package my.norxiva.myrrha.channel;

import my.norxiva.myrrha.channel.bean.Request;
import my.norxiva.myrrha.channel.bean.Response;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface Processor {

    <T extends Response> T execute(Request request) throws ThirdPartyException;

    Map<String, String> generate(Request request) throws ThirdPartyException;

    <T extends Response> T handle(String notification, Function<String, Optional<String>> function)
            throws ThirdPartyException;
}
