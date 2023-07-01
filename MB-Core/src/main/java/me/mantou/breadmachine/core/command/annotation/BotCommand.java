package me.mantou.breadmachine.core.command.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface BotCommand {
    String rootCmd();
    String prefix() default "/";
    String[] alias() default {};
}
