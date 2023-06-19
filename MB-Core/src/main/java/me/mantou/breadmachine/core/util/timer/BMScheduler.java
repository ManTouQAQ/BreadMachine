package me.mantou.breadmachine.core.util.timer;

import com.google.inject.Inject;
import lombok.Getter;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import snw.jkook.scheduler.Task;

@Component(eager = true)
public class BMScheduler {
    @Getter
    private static BMTaskRunner bmTaskRunner;

    public static Task runTaskTimer(Runnable runnable, long delay, long period){
        return bmTaskRunner.runTaskTimer(runnable, delay, period);
    }

    public static Task runTaskLater(Runnable runnable, long delay){
        return bmTaskRunner.runTaskLater(runnable, delay);
    }

    public static Task runTask(Runnable runnable){
        return bmTaskRunner.runTask(runnable);
    }

    @Inject
    private void setBmTaskRunner(BMTaskRunner bmTaskRunner) {
        BMScheduler.bmTaskRunner = bmTaskRunner;
    }
}
