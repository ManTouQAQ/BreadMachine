package me.mantou.breadmachine.core.util.bm;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.core.command.CommandRegister;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.command.wrapper.node.CommandWrapper;
import me.mantou.breadmachine.core.ioc.annotation.Component;
import snw.jkook.event.Listener;
import snw.jkook.plugin.Plugin;

@Component
@Slf4j
public class AutoRegister {
    @Inject
    private Plugin plugin;
    @Inject
    private CommandRegister commandRegister;
    public void scanAndRegister(Injector injector){
        for (Binding<?> value : injector.getBindings().values()) {
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
