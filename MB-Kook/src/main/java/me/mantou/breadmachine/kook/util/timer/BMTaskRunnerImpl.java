package me.mantou.breadmachine.kook.util.timer;

import com.google.inject.Inject;
import me.mantou.breadmachine.kook.BreadMachine;
import me.mantou.breadmachine.kook.ioc.annotation.Component;
import snw.jkook.scheduler.Task;

@Component
public class BMTaskRunnerImpl implements BMTaskRunner{
    @Inject
    private BreadMachine breadMachine;

    @Override
    public Task runTaskTimer(Runnable runnable, long delay, long period) {
        return breadMachine.getCore().getScheduler().runTaskTimer(breadMachine, runnable, delay, period);
    }

    @Override
    public Task runTaskLater(Runnable runnable, long delay) {
        return breadMachine.getCore().getScheduler().runTaskLater(breadMachine, runnable, delay);
    }

    @Override
    public Task runTask(Runnable runnable) {
        return breadMachine.getCore().getScheduler().runTask(breadMachine, runnable);
    }
}
