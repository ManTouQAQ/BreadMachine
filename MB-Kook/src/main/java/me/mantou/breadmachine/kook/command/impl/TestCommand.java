package me.mantou.breadmachine.kook.command.impl;

import lombok.extern.slf4j.Slf4j;
import me.mantou.breadmachine.kook.command.CSender;
import me.mantou.breadmachine.kook.command.annotation.*;
import me.mantou.breadmachine.kook.ioc.annotation.BMIgnore;
import me.mantou.breadmachine.kook.ioc.annotation.BMProperties;
import me.mantou.breadmachine.kook.ioc.annotation.BMValue;
import snw.jkook.entity.User;

@Slf4j
@BMIgnore
@BotCommand(rootCmd = "msg", prefix = ".")
@CmdOpt(helpMsg = "发送私聊消息给指定的用户", descMsg = "发送私聊消息")
@BMProperties(prefix = "msg", properties = {"test.yml"})
public class TestCommand{
    @CmdMethod("send <message> [target]")
    @ParamOpt(key = "target", type = User.class)
    public void sendTempMessageToUser(CSender sender,
                                      @CmdParam("message") String msg,
                                      @CmdParam("target") User target){
        if (target != null){
            target.sendPrivateMessage(msg);
        }else {
            sender.sendTempMessage(msg);
        }
    }
}
