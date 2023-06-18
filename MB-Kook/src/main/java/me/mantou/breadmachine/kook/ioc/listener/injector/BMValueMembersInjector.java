package me.mantou.breadmachine.kook.ioc.listener.injector;

import com.google.inject.MembersInjector;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.ioc.annotation.BMValue;
import me.mantou.breadmachine.kook.util.str.resolver.BMValueResourceResolver;

import java.lang.reflect.Field;

@AllArgsConstructor
@Slf4j
public class BMValueMembersInjector<T> implements MembersInjector<T> {
    private final Field field;

    @Override
    @SneakyThrows
    public void injectMembers(T instance) {
        field.setAccessible(true);
        if (field.get(instance) != null) return;
        BMValueResourceResolver resolver = new BMValueResourceResolver(instance.getClass());
        BMValue fieldAnnotation = field.getAnnotation(BMValue.class);
        String result = resolver.getResult(fieldAnnotation.value());
        if (!field.getType().isAssignableFrom(result.getClass())) {
            log.warn("{}无法进行属性({})的注入，因为类型无法转换 result={}", instance.getClass().getSimpleName(), field.getName(), result);
            return;
        }

        field.set(instance, result);
        log.debug("{}注入属性 {} 的值为 {}", instance.getClass().getSimpleName(), field.getName(), result);
    }
}
