package com.xiaoace.mctokook.listener.minecraft;

import com.xiaoace.mctokook.McToKook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;

import static com.xiaoace.mctokook.McToKook.getKbcClient;

public class OnPlayerMessage implements Listener {

    //mc玩家消息监听器
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecraftPlayerMessage(AsyncPlayerChatEvent asyncPlayerChatEvent){

        if(asyncPlayerChatEvent.isCancelled()){
            return;
        }

        if (McToKook.getInstance().getConfig().getBoolean("To-Kook",true)){
            new BukkitRunnable(){
                @Override
                public void run(){
                    String playerName = asyncPlayerChatEvent.getPlayer().getName();
                    String message = asyncPlayerChatEvent.getMessage();

                    //System.out.println("来自Minecraft的消息: " + message);

                    McToKook.getInstance().getLogger().info("玩家: " +playerName+"说了: "+ message );
                    Channel channel = getKbcClient().getCore().getHttpAPI().getChannel(McToKook.getInstance().getConfig().getString("Channel-ID"));
                    FileConfiguration bukkit_config = McToKook.getInstance().getConfig();
                    String needFormatMessage = bukkit_config.getString("To-Kook-Message","玩家: {playerName} 说: {message}");
                    String formattedMessage = needFormatMessage.replaceAll("\\{playerName}",playerName)
                            .replaceAll("\\{message}",message);
                    if (channel instanceof TextChannel){
                        TextChannel textChannel = (TextChannel) channel;
                        textChannel.sendComponent(formattedMessage,null,null);
                    }
                }
            }.runTaskAsynchronously(McToKook.getInstance());
        }
    }

}
