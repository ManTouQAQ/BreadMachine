package me.mantou.breadmachine.core.command;

import lombok.Data;
import snw.jkook.command.ConsoleCommandSender;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.VoiceChannel;
import snw.jkook.message.Message;
import snw.jkook.message.PrivateMessage;
import snw.jkook.message.TextChannelMessage;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.util.PageIterator;

import java.util.Collection;

@Data
public class CommandSenderWrapper implements CSender {
    private final snw.jkook.command.CommandSender jKookSender;
    private final Message message;

    @Override
    public void sendTempMessage(String msg, Object... params) {
        sendTempMessage(String.format(msg, params));
    }

    @Override
    public String getId(){
        return jKookSender instanceof User s ? s.getId() : "-1";
    }

    @Override
    public String sendTempMessage(BaseComponent component){
        if (jKookSender instanceof ConsoleCommandSender s){
            s.getLogger().info(component.toString());
            return null;
        }else if (jKookSender instanceof User s){
            if (message instanceof PrivateMessage){
                return s.sendPrivateMessage(component);
            }else if (message instanceof TextChannelMessage){
                return ((TextChannelMessage) message).sendToSourceTemp(component);
            }
        }
        return null;
    }

    @Override
    public VoiceChannel getCurrentVoiceChannel(){
        if (!(jKookSender instanceof User user)) return null;
        if (!(message instanceof TextChannelMessage textChannelMessage)) return null;
        PageIterator<Collection<VoiceChannel>> pageIterator = user.getJoinedVoiceChannel(textChannelMessage.getChannel().getGuild());

        while (pageIterator.hasNext()){
            for (VoiceChannel voiceChannel : pageIterator.next()){
                if (voiceChannel.getUsers().stream().anyMatch(u -> user.getId().equals(u.getId()))){
                    return voiceChannel;
                }
            }
        }
        return null;
    }

    @Override
    public void sendTempMessage(String msg) {
        if (jKookSender instanceof ConsoleCommandSender s){
            s.getLogger().info(msg);
        }else if (jKookSender instanceof User s){
            if (message instanceof PrivateMessage){
                s.sendPrivateMessage(msg);
            }else if (message instanceof TextChannelMessage){
                ((TextChannelMessage) message).sendToSourceTemp(msg);
            }
        }
    }
}
