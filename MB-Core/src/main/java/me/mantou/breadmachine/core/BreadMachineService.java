package me.mantou.breadmachine.core;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import me.mantou.breadmachine.core.ioc.module.AutoScanConfig;
import me.mantou.breadmachine.core.ioc.module.BMAutoScanModule;
import me.mantou.breadmachine.core.util.bm.AutoRegister;
import snw.jkook.plugin.BasePlugin;
import snw.jkook.plugin.Plugin;

public class BreadMachineService {
    @Getter
    private static Plugin instance;
    @Getter
    private Injector injector;
    @Inject
    private AutoRegister autoRegister;

    private <T extends BasePlugin> void init(T instance){
        BreadMachineService.instance = instance;
        injector = Guice.createInjector(new BMAutoScanModule(this, instance,
                new AutoScanConfig(this.getClass(), instance.getClass())));
        autoRegister.scanAndRegister(injector);
    }

    public static <T extends BasePlugin> BreadMachineService start(T instance){
        BreadMachineService service = new BreadMachineService();
        service.init(instance);
        return service;
    }
}
