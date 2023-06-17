package me.mantou.breadmachine.kook.service;

import me.mantou.breadmachine.kook.model.ResultData;
import snw.jkook.entity.channel.VoiceChannel;

public interface VoicePlayService {
    ResultData<Void> play(VoiceChannel channel, String value);
}
