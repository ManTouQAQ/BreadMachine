package me.mantou.breadmachine.kook.command.impl;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.util.card.BMCard;
import me.mantou.breadmachine.kook.command.annotation.BotCommand;
import me.mantou.breadmachine.kook.command.annotation.CmdMethod;
import me.mantou.breadmachine.kook.command.annotation.CmdOpt;
import me.mantou.breadmachine.kook.ioc.annotation.PostConstruct;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.event.user.UserClickButtonEvent;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ButtonElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.ActionGroupModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.plugin.Plugin;

import java.util.List;

@BotCommand(rootCmd = "card")
@Slf4j
public class CardCommand {
    @Inject
    private Plugin plugin;

    private BMCard bmCard;

    @PostConstruct
    public void init(){
        bmCard = new BMCard()
                .addButtonListener("v1", click -> {
                    UserClickButtonEvent event = click.getEvent();
                    log.info("{}点击了按钮: {}:{}", event.getUser().getName(), event.getMessageId(), event.getValue());
                    return false;
                })
                .build(plugin);
    }

    @CmdMethod
    @CmdOpt(allowTypes = CmdOpt.CommandType.GROUP)
    public void rootCmd(User user, TextChannel channel){

        MultipleCardComponent component = new CardBuilder().setSize(Size.LG)
                .setTheme(Theme.NONE)
                .addModule(new HeaderModule(user.getName()))
                .addModule(new ActionGroupModule(
                        List.of(new ButtonElement(Theme.INFO, "v1", ButtonElement.EventType.RETURN_VAL, new PlainTextElement("INFO")),
                                new ButtonElement(Theme.PRIMARY, "v2", ButtonElement.EventType.RETURN_VAL, new PlainTextElement("PRIMARY")),
                                new ButtonElement(Theme.DANGER, "v3", ButtonElement.EventType.RETURN_VAL, new PlainTextElement("DANGER")),
                                new ButtonElement(Theme.WARNING, "v4", ButtonElement.EventType.RETURN_VAL, new PlainTextElement("WARNING"))
                        )
                ))
                .addModule(new ActionGroupModule(
                        List.of(new ButtonElement(Theme.SECONDARY, "v5", ButtonElement.EventType.RETURN_VAL, new PlainTextElement("SECONDARY")))
                )).build();
        bmCard.setCard(component).sendTemp(user, channel);
    }


}
