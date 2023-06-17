package me.mantou.breadmachine.kook.util.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snw.jkook.JKook;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.user.UserClickButtonEvent;
import snw.jkook.event.user.UserLeaveGuildEvent;
import snw.jkook.event.user.UserOfflineEvent;
import snw.jkook.message.Message;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BMCard {
    private final CardListener listener = new CardListener();
    private Plugin plugin;
    private BaseComponent card;

    // key是button的value value是对应的listener
    private final Map<String, ButtonListener> buttonListeners = new LinkedHashMap<>();

    // key是频道的id value是对应的messageId
    private final Map<String, String> channelMessageMapping = new HashMap<>(3);

    // key是userId value是对应的tempMessage
    private final Map<String, TempMessage> userTempMessageMapping = new HashMap<>(3);

    // key是userId value是对应的messageId
    private final Map<String, String> userPrivateMessageMapping = new HashMap<>(3);

    public BMCard addButtonListener(String value, ButtonListener listener){
        buttonListeners.put(value, listener);
        return this;
    }

    public BMCard setCard(BaseComponent component){
        this.card = component;
        return this;
    }

    public void sendAll(TextChannel textChannel){
        String messageId = textChannel.sendComponent(card);
        channelMessageMapping.put(textChannel.getId(), messageId);
    }

    public void sendTemp(User user, TextChannel channel){
        String messageId = channel.sendComponent(card, null, user);
        userTempMessageMapping.put(user.getId(), new TempMessage(channel.getId(), messageId));
    }

    public void sendTo(User user){
        String messageId = user.sendPrivateMessage(card);
        userPrivateMessageMapping.put(user.getId(), messageId);
    }

    public BMCard build(Plugin plugin){
        this.plugin = plugin;
        listener.registerListener();
        return this;
    }

    public void destroy(){
        listener.unregisterListener();
    }

    private boolean dispatchButtonClickEvent(ButtonClick event){
        String value = event.getEvent().getValue();
        ButtonListener listener = buttonListeners.get(value);
        if (listener != null){
            return listener.onClick(event);
        }
        return false;
    }

    public abstract class UnregisterableListener implements Listener{
        private boolean registered = false;

        protected void registerListener(){
            if (registered) return;
            plugin.getCore().getEventManager().registerHandlers(plugin, this);
            registered = true;
        }

        protected void unregisterListener(){
            plugin.getCore().getEventManager().unregisterHandlers(this);
            registered = false;
        }
    }

    public class CardListener extends UnregisterableListener{
        @EventHandler
        public void onButtonClick(UserClickButtonEvent event){
            TextChannel channel = event.getChannel();
            String clickedMessageId = event.getMessageId();
            User user = event.getUser();
            String messageId;
            if (channel != null){
                messageId = channelMessageMapping.get(channel.getId());

                if (messageId == null || !messageId.equals(clickedMessageId)) { //不是全体可见的消息
                    //是否为单独可见的消息
                    TempMessage tempMessage = userTempMessageMapping.get(user.getId());
                    if (tempMessage == null) return; //不是这个temp卡片的按钮
                    if (!tempMessage.equals(channel.getId(), clickedMessageId)) return; //不是这个temp卡片的按钮

                    if (dispatchButtonClickEvent(new ButtonClick(event, true))) {
                        userTempMessageMapping.remove(user.getId());
                    }
                    return;
                }

                //是全体可见的消息
                if (dispatchButtonClickEvent(new ButtonClick(event))) {
                    channelMessageMapping.remove(user.getId());
                }
            }else {
                //是私聊消息
                messageId = userPrivateMessageMapping.get(user.getId());
                if (messageId == null || !messageId.equals(clickedMessageId)) return;

                if (dispatchButtonClickEvent(new ButtonClick(event))) {
                    userPrivateMessageMapping.remove(user.getId());
                }
            }
        }

        @EventHandler
        public void onUserLeaveGuild(UserLeaveGuildEvent event){
            userTempMessageMapping.remove(event.getUser().getId());
        }

        @EventHandler
        public void onUserLeave(UserOfflineEvent event) {
            userTempMessageMapping.remove(event.getUser().getId());
        }
    }

    @FunctionalInterface
    public interface ButtonListener {
        /**
         * click button事件
         * @param event 事件的载体
         * @return 如果为true则取消对应(当事人)的监听
         */
        boolean onClick(ButtonClick event);
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class ButtonClick{
        private final UserClickButtonEvent event;
        private boolean tempMsg = false;
        public boolean isPrivate(){
            return event.getChannel() == null;
        }

        @Nullable
        public <T extends Message> T getMessage(){
            if (tempMsg) return null;
            String messageId = event.getMessageId();
            if (isPrivate()){
                return (T) JKook.getHttpAPI().getPrivateMessage(event.getUser(), messageId);
            }else {
                return (T) JKook.getHttpAPI().getTextChannelMessage(messageId);
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class TempMessage{
        private String channelId;
        private String messageId;

        public boolean equals(String channelId, String messageId){
            return this.channelId.equals(channelId) && this.messageId.equals(messageId);
        }
    }
}
