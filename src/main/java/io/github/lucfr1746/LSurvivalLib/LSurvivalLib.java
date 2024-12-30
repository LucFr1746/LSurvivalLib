package io.github.lucfr1746.LSurvivalLib;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerManager;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatisticExpansion;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.LoggerAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class LSurvivalLib extends JavaPlugin {

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).silentLogs(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        new PlayerManager(this);
        new Statistic(this);

        try {
            new PlayerStatisticExpansion(this).register();
            new LoggerAPI(this).success("Successfully hook into PlaceholderAPI!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
