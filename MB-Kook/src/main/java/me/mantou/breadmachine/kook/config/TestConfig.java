package me.mantou.breadmachine.kook.config;

import me.mantou.breadmachine.kook.ioc.annotation.Component;
import me.mantou.breadmachine.kook.ioc.annotation.PostConstruct;

@Component
public class TestConfig {
    @PostConstruct
    public void init(){
        System.out.println("TestConfig init");
    }
}
