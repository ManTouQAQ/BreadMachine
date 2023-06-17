package me.mantou.breadmachine.kook.ioc.module;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
public class AutoScanConfig {
    public final String[] scanPackages;
    public String[] excludePackages = {};

    public AutoScanConfig(Class<?> mainClass) {
        this.scanPackages = new String[]{mainClass.getPackage().getName()};
    }
}
