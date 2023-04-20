package com.xiaoace.mctokook;

import com.xiaoace.mctokook.listener.KookListener;
import com.xiaoace.mctokook.listener.minecraft.OnPlayerJoin;
import com.xiaoace.mctokook.listener.minecraft.OnPlayerMessage;
import com.xiaoace.mctokook.listener.minecraft.OnPlayerQuit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import snw.jkook.JKook;
import snw.jkook.config.InvalidConfigurationException;
import snw.jkook.config.file.YamlConfiguration;
import snw.kookbc.impl.CoreImpl;
import snw.kookbc.impl.KBCClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public final class McToKook extends JavaPlugin implements CommandExecutor{

    private static McToKook INSTANCE;

    public static McToKook getInstance() {
        return INSTANCE;
    }

    private File kbcLocal;

    static KBCClient kbcClient = null;

    //提供一个get KookBC的方法
    public static KBCClient getKbcClient() {
        return kbcClient;
    }

    static String bot_token = "No token provided";
    static String channel_ID = "No channel ID provided";


    @Override
    public void onLoad() {

        INSTANCE = this;
        kbcLocal = new File(getDataFolder(), "kbc.yml");

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
            getLogger().log(Level.SEVERE,"你没有提供bot-token或者bot-token不正确");
            //getLogger().log(Level.SEVERE,bot_token);
            getPluginLoader().disablePlugin(this);
            return;
        } else {
            if (channel_ID.equals("No channel ID provided")){
                getLogger().log(Level.SEVERE,"你没有提供channel ID或channel ID不正确");
                getLogger().log(Level.SEVERE,"你所提供的channel_ID: " + channel_ID);
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

            //成功启动KookBC后就注册Bukkit事件
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(),this);
            Bukkit.getPluginManager().registerEvents(new OnPlayerQuit(),this);
            Bukkit.getPluginManager().registerEvents(new OnPlayerMessage(),this);

            getLogger().info("Kook机器人启动成功");
        }catch (Exception e){
            getLogger().log(Level.SEVERE,"Kook机器人启动失败");
            e.printStackTrace();
        }

        //夏夜说: 不要用InternalPlugin,但是我摆了！
        kbcClient.getCore().getEventManager().registerHandlers(kbcClient.getInternalPlugin(), new KookListener());

    }

    @Override
    public void onDisable() {

        //禁用插件时注销所有事件
        HandlerList.unregisterAll();
        //关闭KookBC
        kbcClient.shutdown();
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
