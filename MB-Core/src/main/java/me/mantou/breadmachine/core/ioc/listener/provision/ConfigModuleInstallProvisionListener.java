package me.mantou.breadmachine.core.ioc.listener.provision;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.spi.ProvisionListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class ConfigModuleInstallProvisionListener implements ProvisionListener {
    private final Binder binder;

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        T instance = provision.provision();
        binder.install((Module) instance);
        log.debug("安装模块: {}", instance.getClass().getSimpleName());
    }
}
