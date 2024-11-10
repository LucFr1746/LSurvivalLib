package io.github.lucfr1746.LLib;

import io.github.lucfr1746.LLib.Player.PlayerManager;
import io.github.lucfr1746.LLib.Utils.Config.Config;
import io.github.lucfr1746.LLib.Utils.Metrics.UpdateChecker;
import io.github.lucfr1746.LLib.Utils.Metrics.bStatsMetrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class LLib extends JavaPlugin {

    private static LLib plugin;

    @Override
    public void onEnable() {
        plugin = this;
    // Metrics
        new UpdateChecker(this);
        new bStatsMetrics(this, 23768);
    // Config
        new Config(this);
    // Registering Player
        new PlayerManager(plugin);
    // Completely register LLib
        Bukkit.getLogger().info("[LLib] Enabled " + plugin + "!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getInstance() {
        return plugin;
    }
}
