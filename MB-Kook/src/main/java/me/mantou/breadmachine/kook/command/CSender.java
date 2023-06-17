package me.mantou.breadmachine.kook.command;

import snw.jkook.entity.channel.VoiceChannel;
import snw.jkook.message.component.BaseComponent;

public interface CSender {
    void sendTempMessage(String msg, Object... params);

    String getId();

    String sendTempMessage(BaseComponent component);

    VoiceChannel getCurrentVoiceChannel();

    void sendTempMessage(String msg);

    snw.jkook.command.CommandSender getJKookSender();

    snw.jkook.message.Message getMessage();
}
