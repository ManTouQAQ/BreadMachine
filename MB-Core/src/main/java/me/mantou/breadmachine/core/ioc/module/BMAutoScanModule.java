package me.mantou.breadmachine.core.ioc.module;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matchers;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.BreadMachineService;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.ioc.annotation.BMIgnore;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import me.mantou.breadmachine.core.ioc.annotation.Configuration;
import me.mantou.breadmachine.core.ioc.listener.provision.PostConstructProvisionListener;
import me.mantou.breadmachine.core.ioc.listener.type.BMValueTypeListener;
import snw.jkook.event.Listener;
import snw.jkook.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class BMAutoScanModule extends AbstractModule {
    private final BreadMachineService machineService;
    private final Plugin plugin;
    private final AutoScanConfig config;
    private final Class<? extends Annotation>[] componentAnnos = new Class[]{
            Component.class,
            BotCommand.class,
            Configuration.class
    };

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new BMValueTypeListener());
        bindListener(Matchers.any(), new PostConstructProvisionListener());

        bind((Class) machineService.getClass()).toInstance(machineService);

        bind((Class) plugin.getClass()).toInstance(plugin);
        bind(Plugin.class).toInstance(plugin);

        ClassGraph graph = new ClassGraph();
        graph.enableAnnotationInfo()
                .acceptPackages(config.scanPackages)
                .rejectPackages(config.excludePackages);

        @Cleanup
        ScanResult scan = graph.scan();
        Arrays.stream(componentAnnos)
                .flatMap(aClass -> scan.getClassesWithAnnotation(aClass).stream())
                .distinct()
                .forEach(classInfo -> {
                    ClassInfoList interfaces = classInfo.getInterfaces();
                    Class loadedClass = classInfo.loadClass();

                    if (loadedClass.getAnnotation(BMIgnore.class) != null) {
                        log.debug("{}因@BMIgnore注解被忽略", loadedClass.getSimpleName());
                        return;
                    }

                    boolean eager = false;

                    Component annotation = (Component) loadedClass.getAnnotation(Component.class);
                    if (annotation != null) eager = annotation.eager();

                    if (interfaces.size() < 1) {
                        bindInstance(loadedClass, eager);
                    } else {
                        if (classInfo.implementsInterface(Module.class)){
                            //加载module
                            Object instance = null;
                            try {
                                instance = loadedClass.getConstructor().newInstance();
                                bind(loadedClass).toInstance(instance);
                                install((Module) instance);
                                log.debug("安装模块: {}", loadedClass.getSimpleName());
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            return;
                        }

                        if (classInfo.implementsInterface(Listener.class)) {
                            bindInstance(loadedClass, eager);
                            return;
                        }

                        //加载普通类
                        for (ClassInfo info : interfaces) {
                            Class<?> aClass = info.loadClass();
                            if (eager){
                                bind(aClass).to(loadedClass).asEagerSingleton();
                            }else {
                                bind(aClass).to(loadedClass).in(Scopes.SINGLETON);
                            }
                        }
                    }
                });
    }

    private void bindInstance(Class loadedClass, boolean eager) {
        if (eager){
            bind(loadedClass).asEagerSingleton();
        }else {
            bind(loadedClass).in(Scopes.SINGLETON);
        }
    }
}
