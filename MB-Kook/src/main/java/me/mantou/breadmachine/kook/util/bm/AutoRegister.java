package me.mantou.breadmachine.kook.util.bm;

import com.google.inject.Binding;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.BreadMachine;
import me.mantou.breadmachine.kook.command.CommandRegister;
import me.mantou.breadmachine.kook.command.annotation.BotCommand;
import me.mantou.breadmachine.kook.command.wrapper.node.CommandWrapper;
import me.mantou.breadmachine.kook.ioc.annotation.Component;
import snw.jkook.event.Listener;

@Component
@Slf4j
public class AutoRegister {
    @Inject
    private BreadMachine plugin;
    @Inject
    private CommandRegister commandRegister;
    public void scanAndRegister(){
        for (Binding<?> value : plugin.getInjector().getBindings().values()) {
            Class<?> type = value.getKey().getTypeLiteral().getRawType();
            if (type.isAnnotationPresent(BotCommand.class)){
                CommandWrapper wrapper = commandRegister.registerCommand(value.getProvider().get());
                if (wrapper != null) log.debug("[自动注册] 命令{}{}成功注册-{}", wrapper.getPrefix(), wrapper.getRootValue(), type.getSimpleName());
            }

            if (Listener.class.isAssignableFrom(type)){
                plugin.getCore().getEventManager().registerHandlers(plugin, (Listener) value.getProvider().get());
                log.debug("[自动注册] 监听器{}成功注册", type.getSimpleName());
            }
        }
    }
}
