package me.mantou.breadmachine.core.util.timer;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import snw.jkook.scheduler.Task;

import javax.annotation.Resource;

@Component
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

    @Resource
    private void setBmTaskRunner(BMTaskRunner bmTaskRunner) {
        BMScheduler.bmTaskRunner = bmTaskRunner;
    }
}
