package io.github.lucfr1746.LLib.Player;

import io.github.lucfr1746.LLib.LLib;
import io.github.lucfr1746.LLib.Utils.Config.ConfigAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class PlayerManager {

    private final File playerFolder;
    private final FileConfiguration playerConfig;
    private final File playerDataFolder;

    public PlayerManager(LLib plugin) {
    Bukkit.getLogger().info("[LLib] Registering Player Manager...");

        Bukkit.getLogger().info("  |- Creating Player essential files...");
        this.playerFolder = new ConfigAPI(plugin).createFolder(plugin.getDataFolder().getPath(), "player");
        this.playerConfig = new ConfigAPI(plugin).createDefaultYamlFileConfiguration(this.playerFolder.getPath(), "player/", "player-config.yml");

        this.playerDataFolder = new ConfigAPI(plugin).createFolder(this.playerFolder.getPath(), "data");

        Bukkit.getLogger().info("[LLib] Completed registering Player Manager!");
    }

    public File getPlayerFolder() {
        return playerFolder;
    }

    public File getPlayerDataFolder() {
        return playerDataFolder;
    }

    public FileConfiguration getPlayerConfig() {
        return this.playerConfig;
    }
}
