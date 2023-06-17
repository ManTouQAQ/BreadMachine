package me.mantou.breadmachine.kook.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.Getter;
import me.mantou.breadmachine.kook.ioc.annotation.BMProperties;
import me.mantou.breadmachine.kook.ioc.annotation.BMValue;
import me.mantou.breadmachine.kook.ioc.annotation.Configuration;
import snw.jkook.Core;
import snw.jkook.HttpAPI;
import snw.jkook.JKook;

@Configuration
@Getter
@BMProperties(properties = "../../kbc.yml")
public class BMConfig extends AbstractModule {
    @BMValue("${token}")
    private String botToken;

    @Provides
    public Core core(){
        return JKook.getCore();
    }

    @Provides
    public HttpAPI httpAPI(){
        return JKook.getHttpAPI();
    }
}