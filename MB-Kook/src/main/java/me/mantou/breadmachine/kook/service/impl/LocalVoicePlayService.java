package me.mantou.breadmachine.kook.service.impl;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.config.BMConfig;
import me.mantou.breadmachine.kook.ioc.annotation.Component;
import me.mantou.breadmachine.kook.ioc.annotation.PostConstruct;
import me.mantou.breadmachine.kook.model.ResultData;
import me.mantou.breadmachine.kook.service.VoicePlayService;
import me.mantou.breadmachine.kook.util.kookvoice.VoiceConnector;
import snw.jkook.entity.channel.VoiceChannel;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.FutureTask;

@Component
@Slf4j
public class LocalVoicePlayService implements VoicePlayService {
    @Inject
    private BMConfig config;
    private static final String FFMPEG_PATH = "D:\\ffmpeg.exe";
    private FFmpegZMQService zmqService = new FFmpegZMQService(FFMPEG_PATH);
    private VoiceConnector connector;

    @PostConstruct
    public void init(){
        connector = new VoiceConnector(config.getBotToken());
    }

    @Override
    @SneakyThrows
    public ResultData<Void> play(VoiceChannel channel, String value) {
        File file = new File(value);
        if (!file.exists()) return ResultData.err("文件" + value + "未找到");

        if (!connector.isInThisChannel(channel)){
            String rtpUrl = connector.connect(channel, () -> null).get();
            zmqService.start(rtpUrl);
            log.debug("ZMQ开启 bind to {}", rtpUrl);
        }

        if (zmqService.isPlaying()) return ResultData.warn("正在播放中，请稍后再试");

        //play mp3 to zmq
        zmqService.playVoice(file, () -> {
            log.debug("播放结束");
            connector.disconnect();
            return null;
        });
        return ResultData.ok("成功播放" + file.getName());
    }

    @Setter
    @Getter
    public static class FFmpegZMQService {
        private static final String START_ZMQ_CMD = "%s -re -loglevel level+info -nostats -stream_loop -1 -i zmq:tcp://127.0.0.1:%d -map 0:a:0 -acodec libopus -ab 128k -filter:a volume=%.1f -ac 2 -ar 48000 -f tee [select=a:f=rtp:ssrc=1357:payload_type=100]%s";
        private static final String PLAY_VOICE_CMD = "%s -re -nostats -i %s -acodec libopus -ab 128k -f mpegts zmq:tcp://127.0.0.1:%d";
        private final String ffmpegPath;
        private Integer zmqPort = 1234;
        private Double volume = 0.2;
        private Process zmqServiceProcess;
        private Process playVoiceProcess;

        public FFmpegZMQService(String ffmpegPath) {
            this.ffmpegPath = ffmpegPath;

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (zmqServiceProcess != null) zmqServiceProcess.destroy();
            }));
        }

        @SneakyThrows
        public void start(String rtpUrl){
            if (zmqServiceProcess != null) zmqServiceProcess.destroy();
            ProcessBuilder processBuilder = new ProcessBuilder(String.format(START_ZMQ_CMD,
                    ffmpegPath,
                    zmqPort,
                    volume,
                    rtpUrl).split(" "));
            processBuilder.redirectErrorStream(true);
            zmqServiceProcess = processBuilder.start();
        }

        public boolean isPlaying(){
            return playVoiceProcess != null && playVoiceProcess.isAlive();
        }

        @SneakyThrows
        public void playVoice(File file, Callable<Void> onEnd){
            if (isPlaying()) return;

            ProcessBuilder processBuilder = new ProcessBuilder(String.format(PLAY_VOICE_CMD,
                    ffmpegPath,
                    file.getAbsolutePath(),
                    zmqPort).split(" "));
            processBuilder.redirectErrorStream(true);
            playVoiceProcess = processBuilder.start();
            ForkJoinPool.commonPool().execute(new FutureTask<Void>(() -> {
                playVoiceProcess.waitFor();
                onEnd.call();
                return null;
            }));
        }
    }
}
