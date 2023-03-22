package com.xiaoace.mctokook.listener.minecraft;

import com.xiaoace.mctokook.McToKook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
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
import static com.xiaoace.mctokook.utils.AllTheTools.getPlayerIconUrl1;

public class OnPlayerQuit implements Listener {

    //mc玩家下线监听器
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecraftPlayerQuit(PlayerQuitEvent playerQuitEvent){

        if (McToKook.getInstance().getConfig().getBoolean("Quit-Message",true)){
            new BukkitRunnable(){
                @Override
                public void run(){

                    String playerName = playerQuitEvent.getPlayer().getName();
                    String playerUUID = playerQuitEvent.getPlayer().getUniqueId().toString();

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

    private MultipleCardComponent buildCard(String playerName, String playerUUID){

        String needFormatMessage = McToKook.getInstance().getConfig().getString("Player-Quit-Message","肝帝{playerName}歇逼了");
        String formattedMessage = needFormatMessage.replaceAll("\\{playerName}",playerName);

        String imageUrl = getPlayerIconUrl1(playerName,playerUUID);

        CardBuilder cardBuilder = new CardBuilder();
        cardBuilder.setTheme(Theme.DANGER).setSize(Size.LG);
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


