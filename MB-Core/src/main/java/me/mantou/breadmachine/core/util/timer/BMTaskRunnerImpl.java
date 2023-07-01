package me.mantou.breadmachine.core.util.timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import snw.jkook.plugin.Plugin;
import snw.jkook.scheduler.Task;

import javax.annotation.Resource;

@Component
public class BMTaskRunnerImpl implements BMTaskRunner{
    @Autowired
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
