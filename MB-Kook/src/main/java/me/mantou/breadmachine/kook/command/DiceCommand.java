package me.mantou.breadmachine.kook.command;

import me.mantou.breadmachine.core.command.CSender;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.command.annotation.CmdMethod;
import me.mantou.breadmachine.core.command.annotation.CmdOpt;
import snw.jkook.entity.channel.TextChannel;

import java.util.Random;

@BotCommand(rootCmd = "dice", alias = {"骰子", "色子"})
public class DiceCommand {
    private final Random random = new Random();
    @CmdMethod
    @CmdOpt(allowTypes = CmdOpt.CommandType.GROUP)
    public void rootCmd(CSender sender, TextChannel channel){
        int nextInt = random.nextInt(6);
        channel.sendComponent(sender.getName(channel.getGuild()) + " 投出了 " + (nextInt + 1));
    }
}
