package me.mantou.breadmachine.kook.service.impl;

import com.sun.net.httpserver.HttpExchange;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import me.mantou.breadmachine.core.ioc.annotation.PostConstruct;
import me.mantou.breadmachine.webhook.ContextHandler;
import me.mantou.breadmachine.webhook.Stream2StringParser;
import me.mantou.breadmachine.webhook.WebhookServer;

import java.util.Map;

@Component(eager = true)
@Slf4j
public class WebHookService {
    @PostConstruct
    public void init(){
        WebhookServer server = new WebhookServer(5678);
        server.addContextHandler(new ContextHandler<String>("/hook") {
            @Override
            @SneakyThrows
            public void handleExchange(HttpExchange exchange, Map<String, String> requestParams, String payload) {
                String topic = requestParams.get("topic");
                if (topic == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                if (topic.equals("github")){
                    log.info(payload);
                }else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
        }.addParser(new Stream2StringParser())
        ).start();
    }
}
