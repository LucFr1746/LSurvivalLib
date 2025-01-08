package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDrowningEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDrowningListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerDrowningListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDrowning(PlayerDrowningEvent event) {
        event.setDamage(5);
        if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
            PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You drowned.", true);
            Bukkit.getPluginManager().callEvent(deathEvent);
        } else {
            event.getPlayer().damage(event.getDamage());
            new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.DROWNING);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDrowning(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
            PlayerDrowningEvent drowningEvent = new PlayerDrowningEvent(player);
            Bukkit.getPluginManager().callEvent(drowningEvent);
        }
    }
}
