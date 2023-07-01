package me.mantou.breadmachine.kook.command;

import me.mantou.breadmachine.core.command.CSender;
import me.mantou.breadmachine.core.command.annotation.BotCommand;
import me.mantou.breadmachine.core.command.annotation.CmdMethod;
import org.springframework.beans.factory.annotation.Autowired;
import snw.jkook.Core;
import snw.jkook.HttpAPI;
import snw.jkook.entity.Guild;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.jkook.util.PageIterator;

import java.util.Collection;

@BotCommand(rootCmd = "binfo")
public class BotInfoCommand {
    @Autowired
    private HttpAPI httpAPI;

    @Autowired
    private Core core;

    @CmdMethod
    public void showInfo(CSender sender){
        CardBuilder cardBuilder = new CardBuilder()
                .setSize(Size.LG)
                .setTheme(Theme.PRIMARY)
                .addModule(new HeaderModule("机器人信息"))
                .addModule(new SectionModule("  机器人名: " + core.getUser().getName()))
                .addModule(new SectionModule("  ID: " + core.getUser().getId()))
                .addModule(new SectionModule("  已加入的群聊:"));

        PageIterator<Collection<Guild>> pageIterator = httpAPI.getJoinedGuilds();
        while (pageIterator.hasNext()){
            Collection<Guild> guilds = pageIterator.next();
            for (Guild guild : guilds){
                cardBuilder.addModule(new SectionModule("    [" + guild.getId() + "] - " + guild.getName()));
            }
        }
        MultipleCardComponent build = cardBuilder.build();
        sender.sendTempMessage(build);
    }

}
