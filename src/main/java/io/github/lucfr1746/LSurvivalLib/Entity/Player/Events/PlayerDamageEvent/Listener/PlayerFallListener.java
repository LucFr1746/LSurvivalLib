package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerFallEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerFallListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerFallListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFallEvent(PlayerFallEvent event) {
        switch (event.getFallDamageCause()) {
            case GROUND -> {
                event.setDamage(Math.max(((event.getFallDistance() - 6.5) * 200/33), 0));
                if (event.getDamage() > 0) {
                    if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
                        PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You fell to your death.", true);
                        Bukkit.getPluginManager().callEvent(deathEvent);
                    } else {
                        event.getPlayer().damage(event.getDamage());
                        new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.FELL);
                    }
                }
            }
            case VOID -> {
                PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You fell into the void.", true);
                Bukkit.getPluginManager().callEvent(deathEvent);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerFallEvent.FallDamageCause fallCause = null;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            fallCause = PlayerFallEvent.FallDamageCause.GROUND;
        } else if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            fallCause = PlayerFallEvent.FallDamageCause.VOID;
        }
        if (fallCause == null) return;
        event.setCancelled(true);

        PlayerFallEvent fallEvent = new PlayerFallEvent(player, player.getFallDistance(), fallCause);
        Bukkit.getPluginManager().callEvent(fallEvent);
    }
}
