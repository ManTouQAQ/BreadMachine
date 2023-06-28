package me.mantou.breadmachine.kook.service;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public interface WebHookPayloadHandler {
    boolean canHandle(String topic);

    default boolean preHandle(HttpExchange exchange, Map<String, String> params, String payload){
        return true;
    }

    void handle(HttpExchange exchange, Map<String, String> params, String payload);
}
