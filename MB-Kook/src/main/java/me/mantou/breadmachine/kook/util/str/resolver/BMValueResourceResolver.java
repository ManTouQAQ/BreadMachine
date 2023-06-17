package me.mantou.breadmachine.kook.util.str.resolver;

import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.BreadMachine;
import me.mantou.breadmachine.kook.ioc.annotation.BMProperties;
import snw.jkook.config.Configuration;
import snw.jkook.config.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class BMValueResourceResolver extends PlaceholderResolver{
    private static final String PARAM_PREFIX = "${";
    private static final String PARAM_SUFFIX = "}";
    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    private String pathPrefix = "";
    private final List<Configuration> configurations = new LinkedList<>();

    public BMValueResourceResolver(Class<?> srcClass) {
        super(PARAM_PREFIX, PARAM_SUFFIX);

        BMProperties annotation = srcClass.getAnnotation(BMProperties.class);

        String[] properties;

        if (annotation == null){
            properties = new String[]{"config.yml"};
        }else {
            if (!Objects.equals(annotation.prefix(), "")) pathPrefix = annotation.prefix().endsWith(".") ? annotation.prefix() : annotation.prefix() + ".";
            properties = annotation.properties();
        }

        File dataFolder = BreadMachine.getInstance().getDataFolder();

        Arrays.stream(properties).forEach(s -> {
            File file = new File(dataFolder, s);
            if (!file.exists()) log.warn("File {} not found", file.getAbsolutePath());
            configurations.add(YamlConfiguration.loadConfiguration(file));
        });
    }

    @Override
    public String getPlaceholderResult(String key) {
        if (key.contains(DEFAULT_VALUE_SEPARATOR)) {
            String[] split = key.split(DEFAULT_VALUE_SEPARATOR);
            Object value = getFromConfigs(split[0]);
            return value == null ? split[1] : objToString(value);
        }
        return objToString(getFromConfigs(key));
    }

    public String objToString(Object obj){
        if (obj == null) return null;
        return obj.toString();
    }

    public Object getFromConfigs(String path){
        for (Configuration configuration : configurations) {
            Object o = configuration.get(pathPrefix + path);
            if (o == null) continue;
            return o;
        }
        log.warn("Cannot find value for path: {}", pathPrefix + path);
        return null;
    }
}
