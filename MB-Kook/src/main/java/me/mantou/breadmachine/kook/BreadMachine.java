package me.mantou.breadmachine.kook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.BreadMachineService;
import snw.jkook.plugin.BasePlugin;

@Slf4j
public class BreadMachine extends BasePlugin {
    @Getter
    private static BreadMachine instance;

    public BreadMachine(){
        instance = this;
    }

    @Override
    public void onEnable() {
        BreadMachineService.start(this);

        log.info("{}-{}已启动", getDescription().getName(), getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        log.info("{}-{}已关闭", getDescription().getName(), getDescription().getVersion());
    }
}
