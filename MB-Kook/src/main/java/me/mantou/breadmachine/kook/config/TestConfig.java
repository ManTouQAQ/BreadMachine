package me.mantou.breadmachine.kook.config;

import me.mantou.breadmachine.core.ioc.annotation.BMValue;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import me.mantou.breadmachine.core.ioc.annotation.PostConstruct;
import me.mantou.breadmachine.kook.model.TestModel;

@Component(eager = true)
public class TestConfig {
    @BMValue("${test}")
    private TestModel model;

    @PostConstruct
    public void init(){
        System.out.println("TestConfig init");
    }
}
