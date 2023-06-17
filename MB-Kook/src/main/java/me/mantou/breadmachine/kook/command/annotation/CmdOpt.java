package me.mantou.breadmachine.kook.command.annotation;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snw.jkook.command.CommandSender;
import snw.jkook.command.ConsoleCommandSender;
import snw.jkook.entity.User;
import snw.jkook.message.Message;
import snw.jkook.message.PrivateMessage;
import snw.jkook.message.TextChannelMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CmdOpt {
    String helpMsg() default "";
    String descMsg() default "";
    CommandType[] allowTypes() default {CommandType.ALL};

    @AllArgsConstructor
    enum CommandType{
        USER(new Class[]{User.class}),
        PRIVATE(new Class[]{User.class}),
        GROUP(new Class[]{User.class}),
        CONSOLE(new Class[]{ConsoleCommandSender.class}),
        ALL(new Class[]{User.class, ConsoleCommandSender.class}),
        ;
        private final Class<? extends CommandSender>[] allowSenders;

        public boolean canExecute(CommandSender sender,
                                  @Nullable Message message){
            if (sender instanceof User){
                if (this == GROUP && message instanceof PrivateMessage){
                    return false;
                }else if (this == PRIVATE && message instanceof TextChannelMessage){
                    return false;
                }
            }

            return Arrays.stream(allowSenders).anyMatch(c -> c.isAssignableFrom(sender.getClass()));
        }
    }
}
