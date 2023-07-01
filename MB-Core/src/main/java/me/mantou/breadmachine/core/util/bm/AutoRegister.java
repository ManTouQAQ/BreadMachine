package me.mantou.breadmachine.core.util.bm;

import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.command.CommandRegister;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.command.wrapper.node.CommandWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import snw.jkook.event.Listener;
import snw.jkook.plugin.Plugin;

import javax.annotation.Resource;
import java.util.Collection;

@Component
@Slf4j
public class AutoRegister {
    @Autowired
    private Plugin plugin;
    @Autowired
    private CommandRegister commandRegister;
    @Autowired
    private ApplicationContext applicationContext;

    public void scanAndRegister(){
        Collection<Object> botCommands = applicationContext.getBeansWithAnnotation(BotCommand.class).values();
        botCommands.forEach(o -> {
            CommandWrapper wrapper = commandRegister.registerCommand(o);
            if (wrapper != null) log.debug("[自动注册] 命令{}{}成功注册-{}", wrapper.getPrefix(), wrapper.getRootValue(), o.getClass().getSimpleName());
        });

        Collection<Listener> eventListeners = applicationContext.getBeansOfType(Listener.class).values();
        eventListeners.forEach(l -> {
            plugin.getCore().getEventManager().registerHandlers(plugin, l);
            log.debug("[自动注册] 监听器{}成功注册", l.getClass().getSimpleName());
        });
    }
}
