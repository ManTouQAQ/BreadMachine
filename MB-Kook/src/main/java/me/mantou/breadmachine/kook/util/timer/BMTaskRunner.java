package me.mantou.breadmachine.kook.util.timer;

import snw.jkook.scheduler.Task;

public interface BMTaskRunner{
    Task runTaskTimer(Runnable runnable, long delay, long period);
    Task runTaskLater(Runnable runnable, long delay);
    Task runTask(Runnable runnable);
}
