package me.mantou.breadmachine.core.command;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.command.wrapper.node.CommandWrapper;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import snw.jkook.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class CommandRegister {
    @Getter
    private final List<CommandWrapper> COMMAND_WRAPPERS = new LinkedList<>();

    @Inject
    private Plugin plugin;

    public CommandWrapper registerCommand(Object command){
        CommandWrapper wrapper = wrapCommand(command);
        if (wrapper == null) return null;
        COMMAND_WRAPPERS.add(wrapper);
        wrapper.register(plugin);
        return wrapper;
    }

    private CommandWrapper wrapCommand(Object command){
        Class<?> commandClass = command.getClass();
        BotCommand botCommandAnno = commandClass.getAnnotation(BotCommand.class);
        if (botCommandAnno == null){
            log.warn("命令类应带有@BotCommand注解-{}", commandClass.getName());
            return null;
        }
        return new CommandWrapper(command);
    }
}
