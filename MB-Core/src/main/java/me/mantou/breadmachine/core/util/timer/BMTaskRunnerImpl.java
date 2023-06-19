package me.mantou.breadmachine.core.util.timer;

import com.google.inject.Inject;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import snw.jkook.plugin.Plugin;
import snw.jkook.scheduler.Task;

@Component
public class BMTaskRunnerImpl implements BMTaskRunner{
    @Inject
    private Plugin plugin;

    @Override
    public Task runTaskTimer(Runnable runnable, long delay, long period) {
        return plugin.getCore().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public Task runTaskLater(Runnable runnable, long delay) {
        return plugin.getCore().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @Override
    public Task runTask(Runnable runnable) {
        return plugin.getCore().getScheduler().runTask(plugin, runnable);
    }
}
