package me.mantou.breadmachine.core.config;

import lombok.Getter;
import me.mantou.breadmachine.core.util.spring.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import snw.jkook.Core;
import snw.jkook.HttpAPI;
import snw.jkook.JKook;

@Configuration
@Getter
@PropertySource(value = "file:kbc.yml", factory = YamlPropertySourceFactory.class)
public class BMConfig {
    @Value("${token}")
    private String botToken;

    @Bean
    public Core core(){
        return JKook.getCore();
    }

    @Bean
    public HttpAPI httpAPI(){
        return JKook.getHttpAPI();
    }
}