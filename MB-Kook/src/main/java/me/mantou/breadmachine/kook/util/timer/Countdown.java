package me.mantou.breadmachine.kook.util.timer;

import snw.jkook.scheduler.Task;

import java.time.Duration;

public abstract class Countdown implements Runnable {

    protected final Integer maxTime;
    protected Duration period = Duration.ofSeconds(1);
    protected int timeLeft;
    private Task task;

    public Countdown(Integer maxTime) {
        this.maxTime = maxTime;
    }

    public Countdown(Integer maxTime, Duration period) {
        this.maxTime = maxTime;
        this.period = period;
    }

    @Override
    public void run() {
        run(timeLeft--);

        if (timeLeft < 0) {
            cancel();
            onFinished();
        }
    }

    protected abstract void run(int timeLeft);

    protected void onFinished(){
    }

    public void start(){
        if (task != null) throw new IllegalStateException("Countdown already started");
        timeLeft = maxTime;
        task = BMScheduler.runTaskTimer(this, 0, period.toMillis());
    }

    public void cancel(){
        if (task == null) throw new IllegalStateException("Countdown not started");
        task.cancel();
    }
}
