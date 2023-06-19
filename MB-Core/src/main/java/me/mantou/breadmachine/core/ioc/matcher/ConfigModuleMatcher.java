package me.mantou.breadmachine.core.ioc.matcher;

import com.google.inject.matcher.AbstractMatcher;

public class ConfigModuleMatcher<T> extends AbstractMatcher<T> {
    @Override
    public boolean matches(T t) {
        return Module.class.isAssignableFrom(t.getClass());
    }
}
