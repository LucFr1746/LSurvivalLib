package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerOnFireEvent;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class FireDamageListener implements Listener {

    private final LSurvivalLib plugin;

    public FireDamageListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerOnFireEvent(PlayerOnFireEvent event) {
        switch (event.getCause()) {
            case CAMPFIRE, FIRE_TICK, HOT_FLOOR -> event.setDamage(5);
            case FIRE -> event.setDamage(10);
            case LAVA -> event.setDamage(20);
        }
        if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
            PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You burned to death.", true);
            Bukkit.getPluginManager().callEvent(deathEvent);
        } else {
            event.getPlayer().damage(event.getDamage());
            new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.FIRE);
        }
    }

    private final Set<Player> fireCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFireDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE) {
            event.setCancelled(true);
            if (!fireCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    fireCooldownSet.remove(player);
                }
            }.runTaskLater(plugin, 10);

            PlayerOnFireEvent fireEvent = new PlayerOnFireEvent(player, PlayerOnFireEvent.FireDamageCause.FIRE);
            Bukkit.getPluginManager().callEvent(fireEvent);
        }
    }

    private final Set<Player> fireTickCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFireTick(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            event.setCancelled(true);
            if (!fireTickCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    fireTickCooldownSet.remove(player);
                }
            }.runTaskLater(plugin, 20);

            PlayerOnFireEvent fireEvent = new PlayerOnFireEvent(player, PlayerOnFireEvent.FireDamageCause.FIRE_TICK);
            Bukkit.getPluginManager().callEvent(fireEvent);
        }
    }

    private final Set<Player> lavaCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLavaDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setCancelled(true);
            if (!lavaCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    lavaCooldownSet.remove(player);
                }
            }.runTaskLater(plugin, 10);

            PlayerOnFireEvent fireEvent = new PlayerOnFireEvent(player, PlayerOnFireEvent.FireDamageCause.LAVA);
            Bukkit.getPluginManager().callEvent(fireEvent);
        }
    }

    private final Set<Player> campfireCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCampfireDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.CAMPFIRE) {
            event.setCancelled(true);
            if (!campfireCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    campfireCooldownSet.remove(player);
                }
            }.runTaskLater(plugin, 10);
            PlayerOnFireEvent fireEvent = new PlayerOnFireEvent(player, PlayerOnFireEvent.FireDamageCause.CAMPFIRE);
            Bukkit.getPluginManager().callEvent(fireEvent);
            event.setCancelled(true);
        }
    }

    private final Set<Player> hotFloorCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHotFloorDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR) {
            event.setCancelled(true);
            if (!hotFloorCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    hotFloorCooldownSet.remove(player);
                }
            }.runTaskLater(plugin, 10);

            PlayerOnFireEvent fireEvent = new PlayerOnFireEvent(player, PlayerOnFireEvent.FireDamageCause.HOT_FLOOR);
            Bukkit.getPluginManager().callEvent(fireEvent);
        }
    }
}
