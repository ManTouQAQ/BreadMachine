package me.mantou.breadmachine.core.ioc.listener.type;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import me.mantou.breadmachine.core.ioc.annotation.BMValue;
import me.mantou.breadmachine.core.ioc.listener.injector.BMValueMembersInjector;

import java.lang.reflect.Field;

public class BMValueTypeListener implements TypeListener {
    @Override
    public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<? super I> rawType = type.getRawType();
        for (Field field : rawType.getDeclaredFields()) {
            if (!field.isAnnotationPresent(BMValue.class)) continue;
            encounter.register(new BMValueMembersInjector<>(field));
        }
    }
}
