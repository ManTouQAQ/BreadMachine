package me.mantou.breadmachine.kook.service.impl;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import me.mantou.breadmachine.kook.service.WebHookPayloadHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class GitHubWebHookPayloadHandler implements WebHookPayloadHandler {

    @Override
    public boolean canHandle(String topic) {
        return topic.equals("github");
    }

    @Override
    public boolean preHandle(HttpExchange exchange, Map<String, String> params, String payload) {
        List<String> list = exchange.getRequestHeaders().get("X-Hub-Signature-256");
        return Objects.equals(list == null ? null : list.get(0), "sha256=3f6fc7b35e1e4c7305d02a15776bc5b3aa6cdd8a80ee2c6022e470f54dad3ab2");
    }

    @Override
    public void handle(HttpExchange exchange, Map<String, String> params, String payload) {

    }
}
