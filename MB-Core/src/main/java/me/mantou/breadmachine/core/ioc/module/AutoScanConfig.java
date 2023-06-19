package me.mantou.breadmachine.core.ioc.module;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AutoScanConfig {
    public final String[] scanPackages;
    public String[] excludePackages = {};

    public AutoScanConfig(Class<?>... mainClasses) {
        this.scanPackages = Arrays.stream(mainClasses).map(c -> c.getPackage().getName()).toArray(String[]::new);
    }
}
