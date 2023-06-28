package me.mantou.breadmachine.core.command;

import snw.jkook.entity.Guild;
import snw.jkook.entity.channel.VoiceChannel;
import snw.jkook.message.component.BaseComponent;

public interface CSender {
    void sendTempMessage(String msg, Object... params);

    String getName();

    String getName(Guild guild);

    String getId();

    String sendTempMessage(BaseComponent component);

    VoiceChannel getCurrentVoiceChannel();

    void sendTempMessage(String msg);

    snw.jkook.command.CommandSender getJKookSender();

    snw.jkook.message.Message getMessage();
}
