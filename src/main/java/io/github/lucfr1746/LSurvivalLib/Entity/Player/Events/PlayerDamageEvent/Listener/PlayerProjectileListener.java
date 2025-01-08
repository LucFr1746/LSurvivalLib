package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.Entity.EntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamagedByProjectileEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatistic;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerProjectileListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerProjectileListener (LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamagedByProjectile(PlayerDamagedByProjectileEvent event) {
        // Handle attacker-less projectiles
//        switch (event.getAttacker()) {
//            case null -> event.setDamage(Math.max(event.getDamage(), new EntityStatistic(event.getProjectile()).getDamage()));
//            // Handle projectile attacks from players
//            case Player attacker -> {
//                PlayerStatistic attackerStats = new PlayerStatistic(attacker);
//                double damage = (attackerStats.getDamage() + new EntityStatistic(event.getProjectile()).getDamage())
//                        * (1 + attackerStats.getStrength() / 100)
//                        * getAdditiveMultiplier()
//                        * getMultiplicativeMultiplier()
//                        + getBonusModifiers();
//
//                boolean isCrit = ThreadLocalRandom.current().nextDouble() <= (attackerStats.getCritChance() / 100);
//                if (isCrit) {
//                    damage *= (1 + attackerStats.getCritDamage() / 100);
//                }
//
//                event.setDamage(damage);
//            }
//            // Handle projectile attacks from living entities
//            case LivingEntity attacker -> event.setDamage(new LivingEntityStatistic(attacker).getDamage());
//            default -> {
//            }
//        }

        // Apply defense reduction to the damage
        PlayerStatistic playerStats = new PlayerStatistic(event.getPlayer());
        event.setDamage(event.getDamage() * (1 - playerStats.getDefense() / (playerStats.getDefense() + 100)));

        // Handle potential player death
        if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
            String deathMessage;

            if (event.getAttacker() instanceof Player attacker) {
                deathMessage = " &c☠ &7You were killed by " + ChatColor.stripColor(attacker.getDisplayName()) + ".";
            } else if (event.getAttacker() instanceof LivingEntity attacker) {
                deathMessage = " &c☠ &7You were killed by " + ChatColor.stripColor(new LivingEntityStatistic(attacker).getDisplayName()) + ".";
            } else {
                deathMessage = " &c☠ &7You were killed.";
            }

            PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), deathMessage, true);
            Bukkit.getPluginManager().callEvent(deathEvent);
        }
        // Apply damage and spawn indicator
        else {
            event.getPlayer().damage(event.getDamage());
            new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.NORMAL);
        }
    }

    private final Set<Player> projectileCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamagedByProjectileAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Projectile projectile)) return;

        event.setCancelled(true);
        if (!projectileCooldownSet.add(player)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                projectileCooldownSet.remove(player);
            }
        }.runTaskLater(this.plugin, 10);
        Object shooter = projectile.getShooter();

        if (shooter instanceof Entity attacker) {
            PlayerDamagedByProjectileEvent projectileEvent = new PlayerDamagedByProjectileEvent(player, projectile, attacker, event.getDamage());
            Bukkit.getPluginManager().callEvent(projectileEvent);
        } else {
            PlayerDamagedByProjectileEvent projectileEvent = new PlayerDamagedByProjectileEvent(player, projectile, null, event.getDamage());
            Bukkit.getPluginManager().callEvent(projectileEvent);
        }
    }

    private double getAdditiveMultiplier() {
        return 1;
    }

    private double getMultiplicativeMultiplier() {
        return 1;
    }

    private double getBonusModifiers() {
        return 0;
    }
}
