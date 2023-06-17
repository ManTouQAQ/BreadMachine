package me.mantou.breadmachine.kook.command;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.BreadMachine;
import me.mantou.breadmachine.kook.command.annotation.BotCommand;
import me.mantou.breadmachine.kook.command.wrapper.node.CommandWrapper;
import me.mantou.breadmachine.kook.ioc.annotation.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class CommandRegister {
    @Getter
    private final List<CommandWrapper> COMMAND_WRAPPERS = new LinkedList<>();

    @Inject
    private BreadMachine plugin;

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
