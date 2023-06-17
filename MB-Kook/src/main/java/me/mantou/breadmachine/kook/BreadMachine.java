package me.mantou.breadmachine.kook;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.command.CommandRegister;
import me.mantou.breadmachine.kook.ioc.module.AutoScanConfig;
import me.mantou.breadmachine.kook.ioc.module.BMAutoScanModule;
import me.mantou.breadmachine.kook.util.bm.AutoRegister;
import snw.jkook.plugin.BasePlugin;

@Slf4j
public class BreadMachine extends BasePlugin {
    @Getter
    private static BreadMachine instance;

    @Getter
    private Injector injector;

    @Inject
    private AutoRegister autoRegister;

    public BreadMachine(){
        instance = this;
    }

    @Override
    public void onLoad() {
        injector = Guice.createInjector(new BMAutoScanModule(this,
                new AutoScanConfig(BreadMachine.class)));
    }

    @Override
    public void onEnable() {
        autoRegister.scanAndRegister();


        log.info("{}-{}已启动", getDescription().getName(), getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        log.info("{}-{}已关闭", getDescription().getName(), getDescription().getVersion());
    }
}
