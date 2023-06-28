package me.mantou.breadmachine.webhook;

import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
@Slf4j
public class WebhookServer {
    private final Integer port;

    private HttpServer httpServer;
    private final List<ContextHandler> contextHandlers = new LinkedList<>();

    public WebhookServer addContextHandler(ContextHandler contextHandler) {
        if (contextHandlers.stream().anyMatch(h -> h.getUrl().equalsIgnoreCase(contextHandler.getUrl()))) return this;
        contextHandlers.add(contextHandler);
        return this;
    }

    @SneakyThrows
    public void start() {
        stop();

        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        contextHandlers.forEach(h -> httpServer.createContext(h.getUrl(), exchange -> {
            if (!exchange.getRequestMethod().equals("POST")){
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            try {
                h.handleExchange(exchange, parseRequestParams(exchange.getRequestURI().getQuery()), h.doParser(exchange.getRequestBody()));
            } catch (Exception e) {
                h.handleException(e);
            }
            if (exchange.getResponseCode() == -1) {
                exchange.sendResponseHeaders(200, -1);
                log.debug("未设置响应代码 自动补全200");
            }
        }));
        httpServer.setExecutor(command -> {
            Thread thread = new Thread(command);
            thread.setName("Web Hook");
            thread.start();
        });

        httpServer.start();
        log.debug("WebHookServer 成功开启");
    }

    @SneakyThrows
    public static Map<String, String> parseRequestParams(String q) {
        Map<String, String> result = new LinkedHashMap<>();
        if (q == null)
            return result;

        int last = 0, next, l = q.length();
        while (last < l) {
            next = q.indexOf('&', last);
            if (next == -1)
                next = l;

            if (next > last) {
                int eqPos = q.indexOf('=', last);
                if (eqPos < 0 || eqPos > next)
                    result.put(URLDecoder.decode(q.substring(last, next), "utf-8"), "");
                else
                    result.put(URLDecoder.decode(q.substring(last, eqPos), "utf-8"), URLDecoder.decode(q.substring(eqPos + 1, next), "utf-8"));
            }
            last = next + 1;
        }
        return result;
    }

    public static void main(String[] args) {
        WebhookServer webhookServer = new WebhookServer(5678);
        webhookServer.start();
    }

    public void stop() {
        if (httpServer == null) return;
        httpServer.stop(0);
        log.debug("WebHookServer 成功关闭");
    }

}
