package me.mantou.breadmachine.kook.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BMProperties {

    String prefix() default "";
    String[] properties() default {"config.yml"};
}
