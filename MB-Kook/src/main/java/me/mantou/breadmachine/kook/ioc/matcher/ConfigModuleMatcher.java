package me.mantou.breadmachine.kook.ioc.matcher;

import com.google.inject.matcher.AbstractMatcher;
import me.mantou.breadmachine.kook.ioc.annotation.Configuration;

public class ConfigModuleMatcher<T> extends AbstractMatcher<T> {
    @Override
    public boolean matches(T t) {
        return Module.class.isAssignableFrom(t.getClass());
    }
}
