import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Named;
import com.google.inject.spi.ProvisionListener;
import lombok.Data;
import lombok.SneakyThrows;
import me.mantou.breadmachine.kook.service.impl.LocalVoicePlayService;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Test {
    @SneakyThrows
    public static void main(String[] args) {
        LocalVoicePlayService.FFmpegZMQService zmqService = new LocalVoicePlayService.FFmpegZMQService("D:\\ffmpeg.exe");
        zmqService.start("rtp://82.157.157.240:35044?rtcpport=40257");
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            zmqService.playVoice(new File("D:\\123.mp3"), () -> {
                System.out.println("播放结束" + finalI);
                return null;
            });
        }

        TimeUnit.MINUTES.sleep(1);
        System.out.println("end");


//        System.out.println(String.format("123 %s sada%.2f %s", "我", 0.2, "你"));
    }


    public static void main1(String[] args) {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestDao.class).to(TestDaoImpl.class).in(Scopes.SINGLETON);
                bind(TestService.class).to(TestServiceImpl.class).in(Scopes.SINGLETON);
                bindListener(Matchers.any(), new ProvisionListener() {
                    @Override
                    @SneakyThrows
                    public <T> void onProvision(ProvisionInvocation<T> provision) {
                        T instance = provision.provision();
                        System.out.println(instance);
                    }
                });
            }

            @Provides
            @Named("todo")
            private String todo(){
                return "123456";
            }
        });

        TestService testService = injector.getInstance(TestService.class);
        System.out.println("---");
        System.out.println(testService);
        testService.print();
    }


    @Data
    public static class TestServiceImpl implements TestService{
        @Inject
        private TestDao dao;

        @Override
        public void print() {
            System.out.println(dao.getTodo());
        }
    }

    public interface TestService{
        void print();
    }

    @Data
    public static class TestDaoImpl implements TestDao{
        @Inject
        @Named("todo")
        private String todo;


        @Override
        public String getTodo() {
            return todo;
        }
    }

    public interface TestDao{
        String getTodo();
    }
}