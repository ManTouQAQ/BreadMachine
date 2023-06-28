package me.mantou.breadmachine.kook.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.sun.net.httpserver.HttpExchange;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import me.mantou.breadmachine.core.ioc.annotation.PostConstruct;
import me.mantou.breadmachine.kook.model.dto.GitHubWebHookPayload;
import me.mantou.breadmachine.kook.service.WebHookPayloadHandler;
import me.mantou.breadmachine.webhook.ContextHandler;
import me.mantou.breadmachine.webhook.Stream2StringParser;
import me.mantou.breadmachine.webhook.WebhookServer;

import java.util.Map;

@Component(eager = true)
@Slf4j
public class WebHookService {
    @Inject
    private WebHookPayloadHandler handler;

    //4905d5ff-7357-46ee-afd3-d6b11dd0e9f3
    @PostConstruct
    public void init(){
        WebhookServer server = new WebhookServer(5678);
        server.addContextHandler(new ContextHandler<String>("/hook") {
            @Override
            public void handleExchange(HttpExchange exchange, Map<String, String> requestParams, String payload) throws Exception{
                String topic = requestParams.get("topic");
                if (topic == null) {
                    log.debug("hook topic unknown...");
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                if (handler.canHandle(topic)){
                    if (handler.preHandle(exchange, requestParams, payload))
                        handler.handle(exchange, requestParams, payload);
                }else {
                    exchange.sendResponseHeaders(404, -1);
                }
            }
        }.addParser(new Stream2StringParser())
        ).start();
    }
}
