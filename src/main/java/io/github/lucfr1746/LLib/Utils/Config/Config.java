package io.github.lucfr1746.LLib.Utils.Config;

import io.github.lucfr1746.LLib.LLib;
import io.github.lucfr1746.LLib.Utils.LException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private FileConfiguration config;

    public Config(LLib plugin) {
        Bukkit.getLogger().info("[LLib] Initializing config...");
        try {
            config = new ConfigAPI(plugin)
                    .createDefaultYamlFileConfiguration(plugin.getDataFolder().getPath(), "", "config.yml");
            readConfig();
        } catch (LException.PluginNotFoundException | LException.FileNotFoundException | LException.CannotCreateFileException e) {
            throw new LException.ConfigInitializationException("Failed to initialize Config");
        }
        readConfig();
    }

    private void readConfig() {

    }
}
