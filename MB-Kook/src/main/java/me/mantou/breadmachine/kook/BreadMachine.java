package me.mantou.breadmachine.kook;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.BreadMachineService;
import snw.jkook.plugin.BasePlugin;

@Slf4j
public class BreadMachine extends BasePlugin {
    @Getter
    private static BreadMachine instance;

    @Getter
    private BreadMachineService machineService;

    public BreadMachine(){
        instance = this;
    }

    @Override
    public void onLoad() {
        machineService = BreadMachineService.start(this);
    }

    @Override
    public void onEnable() {
        machineService.scanAndRegister();

        log.info("{}-{}已启动", getDescription().getName(), getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        log.info("{}-{}已关闭", getDescription().getName(), getDescription().getVersion());
    }
}
