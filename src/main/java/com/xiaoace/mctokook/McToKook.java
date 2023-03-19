package com.xiaoace.mctokook;

import com.xiaoace.mctokook.listener.KookListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import snw.jkook.JKook;
import snw.jkook.config.InvalidConfigurationException;
import snw.jkook.config.file.YamlConfiguration;
import snw.jkook.entity.channel.Channel;
import snw.jkook.entity.channel.TextChannel;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;
import snw.kookbc.impl.plugin.InternalPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class McToKook extends JavaPlugin implements Listener, CommandExecutor{

    private static McToKook INSTANCE;

    public static McToKook getInstance() {
        return INSTANCE;
    }

    private File kbcLocal;

    static KBCClient kbcClient = null;
    static String bot_token = "No token provided";
    static String channel_ID = "No channel ID provided";

    @Override
    public void onLoad() {

        INSTANCE = this;
        kbcLocal = new File(getDataFolder(), "kbc.yml"); // use your plugin data folder instead of global! -- SNWCreations

        //bukkit保存基础配置文件
        saveDefaultConfig();
        //bukkit重新加载
        reloadConfig();

        //KookBC保存基础配置文件
        saveKBCConfig();

        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(kbcLocal);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        CoreImpl core = new CoreImpl();
        JKook.setCore(core);

        FileConfiguration bukkit_config = getConfig();
        //token
        bot_token = bukkit_config.getString("bot-token","No token provided");
        //channel_ID
        channel_ID = bukkit_config.getString("Channel-ID","No channel ID provided");

        if (bot_token.equals("No token provided")){
            getLogger().error("你没有提供bot-token或者bot-token不正确");
            getLogger().error(bot_token);
            getPluginLoader().disablePlugin(this);
            return;
        } else {
            if (channel_ID.equals("No channel ID provided")){
                getLogger().error("你没有提供channel ID或channel ID不正确");
                getLogger().error(channel_ID);
                getPluginLoader().disablePlugin(this);
                return; // do not waste memory for constructing KBCClient! -- SNWCreations
            }
        }

        kbcClient = new KBCClient(core,config,null,bot_token);

    }

    @Override
    public void onEnable() {

        //尝试启动KookBC
        try{
            getLogger().info("Kook机器人启动中");
            kbcClient.start();
            Bukkit.getPluginManager().registerEvents(this,this);
            getLogger().info("Kook机器人启动成功");
        }catch (Exception e){
            getLogger().error("Kook机器人启动失败");
            e.printStackTrace();
        }

        //夏夜说: 不要用InternalPlugin,但是我摆了！-
        // why don't you read KBCClient source code before you writing this??? -- SNWCreations
        kbcClient.getCore().getEventManager().registerHandlers(kbcClient.getInternalPlugin(), new KookListener());

    }

    @Override
    public void onDisable() {

        //禁用插件时注销所有事件
        HandlerList.unregisterAll((Listener) this);

        //关闭KookBC
        kbcClient.shutdown();
    }

    //mc玩家消息监听器
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecraftPlayerMessage(AsyncPlayerChatEvent asyncPlayerChatEvent){

        if(asyncPlayerChatEvent.isCancelled()){
            return;
        }

        if (getConfig().getBoolean("To-Kook",true)){
            new BukkitRunnable(){
                @Override
                public void run(){
                    String playerName = asyncPlayerChatEvent.getPlayer().getName();
                    String message = asyncPlayerChatEvent.getMessage();
                    getLogger().info("玩家: " +playerName+"说了: "+ message );
                    Channel channel = kbcClient.getCore().getHttpAPI().getChannel(getConfig().getString("Channel-ID"));
                    FileConfiguration bukkit_config = getConfig();
                    String needFormatMessage = bukkit_config.getString("To-Kook-Message","玩家: {playerName} 说: {message}");
                    String formattedMessage = needFormatMessage.replaceAll("\\{playerName}",playerName)
                            .replaceAll("\\{message}",message);
                    if (channel instanceof TextChannel){
                        TextChannel textChannel = (TextChannel) channel;
                        textChannel.sendComponent(formattedMessage,null,null);
                    }
                }
            }.runTaskAsynchronously(this);
        }
    }

    //mc玩家上下线监听器
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecraftPlayerLogin(PlayerJoinEvent playerJoinEvent){

        if (getConfig().getBoolean("Join-Message",true)){
            new BukkitRunnable(){
                @Override
                public void run(){
                    String playerName = playerJoinEvent.getPlayer().getName();
                    String needFormatMessage = getConfig().getString("Player-Join-Message","{playerName}偷偷的溜进了服务器");
                    String formattedMessage = needFormatMessage.replaceAll("\\{playerName}",playerName);
                    Channel channel = kbcClient.getCore().getHttpAPI().getChannel(getConfig().getString("Channel-ID"));
                    if (channel instanceof TextChannel){
                        TextChannel textChannel = (TextChannel) channel;
                        textChannel.sendComponent(formattedMessage,null,null);
                    }
                }
            }.runTaskAsynchronously(this);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMinecraftPlayerQuit(PlayerQuitEvent playerQuitEvent){

        if (getConfig().getBoolean("Quit-Message",true)){
            new BukkitRunnable(){
                @Override
                public void run(){
                    String playerName = playerQuitEvent.getPlayer().getName();
                    String needFormatMessage = getConfig().getString("Player-Quit-Message","肝帝{playerName}歇逼了");
                    String formattedMessage = needFormatMessage.replaceAll("\\{playerName}",playerName);
                    Channel channel = kbcClient.getCore().getHttpAPI().getChannel(getConfig().getString("Channel-ID"));
                    if (channel instanceof TextChannel){
                        TextChannel textChannel = (TextChannel) channel;
                        textChannel.sendComponent(formattedMessage,null,null);
                    }
                }
            }.runTaskAsynchronously(this);
        }
    }



    //KookBC保存配置文件 爱来自夏夜
    private void saveKBCConfig() {
        try (final InputStream stream = McToKook.class.getResourceAsStream("/kbc.yml")) {
            if (stream == null) {
                throw new Error("Unable to find kbc.yml");
            }
            if (kbcLocal.exists()) {
                return;
            }
            //noinspection ResultOfMethodCallIgnored
            kbcLocal.createNewFile();

            try (final FileOutputStream out = new FileOutputStream(kbcLocal)) {
                int index;
                byte[] bytes = new byte[1024];
                while ((index = stream.read(bytes)) != -1) {
                    out.write(bytes, 0, index);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
