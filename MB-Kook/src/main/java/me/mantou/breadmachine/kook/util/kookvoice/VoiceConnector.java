package me.mantou.breadmachine.kook.util.kookvoice;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import snw.jkook.entity.channel.VoiceChannel;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class VoiceConnector {
    @Getter
    private String channelId;
    private final String token;
    private WebSocket webSocket;

    public VoiceConnector(String token) {
        this.token = token;
    }

    public Future<String> connect(VoiceChannel voiceChannel, Callable<Void> onDead) throws IllegalStateException {
        disconnect(); // make sure the connection actually dead, or something unexpected will happen?
        this.channelId = voiceChannel.getId();
        webSocket = null; // help GC

        OkHttpClient client = new OkHttpClient.Builder().pingInterval(30, TimeUnit.SECONDS).build();

        // region Get Gateway
        String gatewayWs;
        String fullGatewayUrl = "https://www.kookapp.cn/api/v3/gateway/voice?channel_id=" + voiceChannel.getId();
        try (Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url(fullGatewayUrl)
                        .addHeader("Authorization", String.format("Bot %s", token))
                        .build()
        ).execute()) {
            if (response.code() != 200) {
                throw new IllegalStateException();
            }
            assert response.body() != null;
            JsonObject element = JsonParser.parseString(response.body().string()).getAsJsonObject();
            if (element.get("code").getAsInt() != 0) {
                throw new IllegalStateException();
            }
            gatewayWs = element.getAsJsonObject("data").get("gateway_url").getAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture<String> future = new CompletableFuture<>();

        // endregion
        webSocket = client.newWebSocket(
                new Request.Builder()
                        .url(gatewayWs)
                        .build(),
                new VoiceWebSocketListener(future, onDead)
        );

        webSocket.send(randomId(VoiceConstants.STAGE_1));
        webSocket.send(randomId(VoiceConstants.STAGE_2));
        webSocket.send(randomId(VoiceConstants.STAGE_3));
        return future;
    }

    public void disconnect() {
        channelId = null;
        if (webSocket != null) {
            webSocket.close(1000, "User Closed Service");
        }
    }

    public boolean isInThisChannel(VoiceChannel channel){
        return Objects.equals(channelId, channel.getId());
    }

    private static String randomId(String constant) {
        JsonObject object = JsonParser.parseString(constant).getAsJsonObject();
        object.remove("id");
        object.addProperty("id", new SecureRandom().nextInt(8999999) + 1000000);
        return new Gson().toJson(object);
    }
}
