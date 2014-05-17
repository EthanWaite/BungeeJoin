package io.github.dead_i.bungeejoin;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BungeeJoin extends Plugin implements Listener {
    private static Configuration config;

    public void onEnable() {
        loadConfig();
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new Command("reloadjoin", "bungeejoin.reload") {
            @Override
            public void execute(CommandSender sender, String[] strings) {
                sender.sendMessage(new TextComponent("Reloading config..."));
                loadConfig();
            }
        });
    }

    public void loadConfig() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                ByteStreams.copy(getResourceAsStream("config.yml"), new FileOutputStream(configFile));
                getLogger().warning("A new configuration file has been created. Please edit config.yml and run /reloadjoin.");
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        for (Object cmd : config.getList("join")) {
            getProxy().getPluginManager().dispatchCommand(event.getPlayer(), cmd.toString());
        }
    }
}
