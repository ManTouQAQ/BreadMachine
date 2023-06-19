package me.mantou.breadmachine.core.command.annotation;

import me.mantou.breadmachine.core.command.wrapper.pb.DefParamBuilder;
import me.mantou.breadmachine.core.command.wrapper.pb.NullDefParamBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamOpt {
    String key();
    Class<?> type() default String.class;
    Class<? extends DefParamBuilder> defaultValue() default NullDefParamBuilder.class;
}
