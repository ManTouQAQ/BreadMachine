package me.mantou.breadmachine.kook.ioc.listener.provision;

import com.google.inject.spi.ProvisionListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.ioc.annotation.PostConstruct;

import java.lang.reflect.Method;

@Slf4j
public class PostConstructProvisionListener implements ProvisionListener {
    @Override
    @SneakyThrows
    public <T> void onProvision(ProvisionInvocation<T> provision) {
        T instance = provision.provision();
        Class<?> aClass = instance.getClass();
        //初始化方法
        for (Method method : aClass.getDeclaredMethods()) {
            method.setAccessible(true);
            if (!method.isAnnotationPresent(PostConstruct.class)) continue;
            method.invoke(instance);
            log.debug("{}初始化方法{}执行完毕", aClass.getSimpleName(), method.getName());
            break;
        }
    }
}
