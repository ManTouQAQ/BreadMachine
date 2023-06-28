package me.mantou.breadmachine.webhook;

import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public abstract class ContextHandler<T> {
    private final String url;
    private final List<PayloadParser> payloadParsers = new LinkedList<>();
    public ContextHandler<T> addParser(PayloadParser parser){
        payloadParsers.add(parser);
        return this;
    }

    public abstract void handleExchange(HttpExchange exchange, Map<String, String> requestParams, T payload);

    protected T doParser(InputStream inputStream){
        Object payload = inputStream;
        for (PayloadParser parser : payloadParsers) {
            payload = parser.parse(payload);
        }
        return (T) payload;
    }
}
