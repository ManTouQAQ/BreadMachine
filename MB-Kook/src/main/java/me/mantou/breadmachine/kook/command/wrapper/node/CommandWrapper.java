package me.mantou.breadmachine.kook.command.wrapper.node;

import lombok.Getter;
import me.mantou.breadmachine.kook.command.annotation.BotCommand;
import me.mantou.breadmachine.kook.command.annotation.CmdMethod;
import me.mantou.breadmachine.kook.command.annotation.CmdOpt;
import me.mantou.breadmachine.kook.command.wrapper.CmdExecutor;
import snw.jkook.command.JKookCommand;
import snw.jkook.message.Message;
import snw.jkook.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class CommandWrapper extends CommandNode{
    private final String prefix;
    private final Object commandInstance;
    private CmdOpt.CommandType[] globalAllowTypes = {CmdOpt.CommandType.ALL};

    public boolean isGlobalAllowType(snw.jkook.command.CommandSender sender, Message msg){
        return Arrays.stream(globalAllowTypes).anyMatch(t -> t.canExecute(sender, msg));
    }

    public CommandWrapper(Object commandInstance) {
        this.commandInstance = commandInstance;
        Class<?> instanceClass = commandInstance.getClass();
        BotCommand botCommandAnno = instanceClass.getAnnotation(BotCommand.class);
        CmdOpt optAnno = instanceClass.getAnnotation(CmdOpt.class);

        setRootValue(botCommandAnno.rootCmd());
        this.prefix = botCommandAnno.prefix();
        for (String alias : botCommandAnno.alias()) {
            addAlias(alias);
        }

        if (optAnno != null){
            setDescription(optAnno.descMsg());
            setHelpMessage(optAnno.helpMsg());
            globalAllowTypes = optAnno.allowTypes();
        }

        setRootWrapper(this);
        wrapperMethods();
    }


    private void wrapperMethods(){
        for (Method method : commandInstance.getClass().getDeclaredMethods()) {
            CmdMethod cmdMethodAnno = method.getAnnotation(CmdMethod.class);
            if (cmdMethodAnno == null) continue;

            String value = cmdMethodAnno.value();
            if (value.isEmpty()){
                setExecutor(new CmdExecutor(commandInstance, method));
                continue;
            }

            wrapperMethod(this, Arrays.stream(value.trim().split(" ")).toList(), method, commandInstance);
        }
    }

    public void register(Plugin plugin){
        JKookCommand jKookCommand = build();
        jKookCommand.addPrefix(this.prefix);
        jKookCommand.register(plugin);
    }
}
