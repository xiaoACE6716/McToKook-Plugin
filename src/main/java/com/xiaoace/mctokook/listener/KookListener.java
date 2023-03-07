package com.xiaoace.mctokook.listener;

import com.xiaoace.mctokook.McToKook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.configuration.file.FileConfiguration;
import snw.jkook.entity.User;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.message.TextChannelMessage;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.TextComponent;

import static com.xiaoace.mctokook.utils.MinecraftTextConverter.convertToMinecraftFormat;

public class KookListener implements Listener {

    FileConfiguration bukkit_config = McToKook.getInstance().getConfig();

    //Kook消息监听器
    @EventHandler()
    public void onKookMessage(ChannelMessageEvent channelMessageEvent){

        if (!bukkit_config.getBoolean("To-Minecraft",true)){
            return;
        }

        User user = null;
        TextChannelMessage message = null;
        String user_uuid = channelMessageEvent.getMessage().getSender().getId();

        if (channelMessageEvent.getChannel().getId().equals(McToKook.getInstance().getConfig().getString("Channel-ID"))){
            //消息的发送者
            user = channelMessageEvent.getMessage().getSender();
            //消息
            message = channelMessageEvent.getMessage();
        }

        String user_nickname = user.getNickName(channelMessageEvent.getChannel().getGuild());
        BaseComponent component = message.getComponent();

        //将要发到服务器的消息

        if (component instanceof TextComponent){

            TextComponent textComponent = ((TextComponent) component);

            //FileConfiguration bukkit_config = McToKook.getInstance().getConfig();
            String needFormatMessage = bukkit_config.getString("To-Minecraft-Message","用户: {nickName} 说: {message}");

            String formattedMessage = needFormatMessage.replaceAll("\\{nickName}",user_nickname)
                    .replaceAll("\\{message}",convertToMinecraftFormat(textComponent.toString()));

            if (!formattedMessage.trim().isEmpty()){

                String clickEventValue = String.format("(met)%s(met)",user_uuid);

                String hoverText = "点击以快速回复Kook的消息,注意: 会直接覆盖聊天栏！！！";

                net.md_5.bungee.api.chat.TextComponent ct = new net.md_5.bungee.api.chat.TextComponent(formattedMessage);
                ct.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,clickEventValue));

                //HoverEvent(Action, BaseComponent[])已弃用，转为使用HoverEvent(Action, Content[])
                //ct.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(hoverText).create()));

                Text text = new Text(new ComponentBuilder(hoverText).create());
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,text);

                ct.setHoverEvent(hoverEvent);

                McToKook.getInstance().getServer().spigot().broadcast(ct);

            }
        }
    }
}
