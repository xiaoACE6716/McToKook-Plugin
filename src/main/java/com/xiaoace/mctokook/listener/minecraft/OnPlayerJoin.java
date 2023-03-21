package com.xiaoace.mctokook.listener.minecraft;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.xiaoace.mctokook.McToKook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import snw.jkook.entity.abilities.Accessory;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.module.SectionModule;

import static com.xiaoace.mctokook.McToKook.getKbcClient;
import static com.xiaoace.mctokook.utils.AllTheTools.getPlayerIconUrl;

public class OnPlayerJoin implements Listener {

    //mc玩家上线监听器
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecraftPlayerLogin(PlayerJoinEvent playerJoinEvent){

        if (McToKook.getInstance().getConfig().getBoolean("Join-Message",true)){
            new BukkitRunnable(){
                @Override
                public void run(){
                    String playerName = playerJoinEvent.getPlayer().getName();
                    String playerUUID = playerJoinEvent.getPlayer().getUniqueId().toString();

                    MultipleCardComponent cardComponent = buildCard(playerName,playerUUID);

                    Channel channel = getKbcClient().getCore().getHttpAPI().getChannel(McToKook.getInstance().getConfig().getString("Channel-ID"));
                    if (channel instanceof TextChannel){
                        TextChannel textChannel = (TextChannel) channel;
                        textChannel.sendComponent(cardComponent,null,null);
                    }
                }
            }.runTaskAsynchronously(McToKook.getInstance());
        }
    }

    private MultipleCardComponent buildCard(String playerName,String playerUUID){

        String needFormatMessage = McToKook.getInstance().getConfig().getString("Player-Join-Message","{playerName}偷偷的溜进了服务器");
        String formattedMessage = needFormatMessage.replaceAll("\\{playerName}",playerName);

        String imageUrl = getPlayerIconUrl(playerName,playerUUID);

        CardBuilder cardBuilder = new CardBuilder();
        cardBuilder.setTheme(Theme.SUCCESS).setSize(Size.LG);
        cardBuilder.addModule(
                new SectionModule(
                        new MarkdownElement(formattedMessage),
                        new ImageElement(imageUrl,playerName,Size.SM,false),
                        Accessory.Mode.LEFT
                )
        );

        return cardBuilder.build();
    }

}
