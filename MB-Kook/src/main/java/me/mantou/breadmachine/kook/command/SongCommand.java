package me.mantou.breadmachine.kook.command;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.command.CSender;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.command.annotation.CmdMethod;
import me.mantou.breadmachine.core.command.annotation.CmdOpt;
import me.mantou.breadmachine.core.command.annotation.CmdParam;
import me.mantou.breadmachine.kook.service.VoicePlayService;
import org.springframework.beans.factory.annotation.Autowired;
import snw.jkook.entity.channel.VoiceChannel;

import javax.annotation.Resource;

@BotCommand(rootCmd = "song", alias = {"音乐"})
@CmdOpt(helpMsg = "播放音乐", descMsg = "播放音乐")
@Slf4j
public class SongCommand{

    @Autowired
    private VoicePlayService voicePlayService;

    @CmdMethod("<type> <value>")
    @SneakyThrows
    public void showSong(CSender sender,
                         @CmdParam("type") String type,
                         @CmdParam("value") String value) {

        VoiceChannel voiceChannel = sender.getCurrentVoiceChannel();
        if (voiceChannel == null) {
            sender.sendTempMessage("您当前并未在语音频道中");
            return;
        }
//        String rtpUrl = connector.connect(voiceChannel, () -> null).get();
//        System.out.println(rtpUrl);

        voicePlayService.play(voiceChannel, value).check(data -> {
            sender.sendTempMessage(data.getMsg());
            return null;
        });
    }
}
