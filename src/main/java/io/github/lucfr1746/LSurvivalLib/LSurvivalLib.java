package io.github.lucfr1746.LSurvivalLib;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.github.lucfr1746.LSurvivalLib.Entity.Events.EventsManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LSurvivalLib extends JavaPlugin {

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        new EventsManager(this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }
}
