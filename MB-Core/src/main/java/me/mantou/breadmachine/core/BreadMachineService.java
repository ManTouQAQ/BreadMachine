package me.mantou.breadmachine.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.util.bm.AutoRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import snw.jkook.plugin.BasePlugin;

@Service
@Slf4j
public class BreadMachineService {

    @Autowired
    private AutoRegister autoRegister;

    public void scanAndRegister(){
        autoRegister.scanAndRegister();
    }

    public static <T extends BasePlugin> BreadMachineService start(T instance){
        ApplicationContext applicationContext = init(instance);
        BreadMachineService service = applicationContext.getBean(BreadMachineService.class);
        log.info("面包机服务启动成功");
        service.scanAndRegister();
        log.info("面包机自动注册服务完成");
        return service;
    }

    @SneakyThrows
    private static <T extends BasePlugin> ApplicationContext init(T instance){

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setClassLoader(instance.getClass().getClassLoader());

        context.getBeanFactory().registerSingleton(instance.getClass().getSimpleName(), instance);

        context.scan(BreadMachineService.class.getPackageName(),
                instance.getClass().getPackageName());
        context.refresh();
        return context;
    }
}
