package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamagedByExplosionEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerExplosionListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerExplosionListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamagedByExplosion(PlayerDamagedByExplosionEvent event) {
        switch (event.getExplosionDamageCause()) {
            case CREEPER -> {
                double damage = new LivingEntityStatistic((LivingEntity) event.getAttacker()).getDamage();
                event.setDamage(Math.max(damage, event.getDamage()));
            }
            case BAD_RESPAWN_POINT, FIREWORK_ROCKET, FIREBALL, SMALL_FIREBALL -> event.setDamage(event.getDamage() * 200/33);
            case TNT, TNT_MINECART -> event.setDamage(55);
            case END_CRYSTAL -> event.setDamage(100);
        }
        if (event.getDamage() <= 0) return;
        if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
            PlayerGoingToDeathEvent deathEvent;
            if (event.getExplosionDamageCause() == PlayerDamagedByExplosionEvent.ExplosionDamageCause.CREEPER) {
                deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were killed by " + ChatColor.stripColor(new LivingEntityStatistic((LivingEntity) event.getAttacker()).getDisplayName()) + ".", true);
            } else {
                deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were blown up.", true);
            }
            Bukkit.getPluginManager().callEvent(deathEvent);
        } else {
            event.getPlayer().damage(event.getDamage());
            new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.EXPLOSION);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            Entity damager = event.getDamager();
            if (damager.getType() == EntityType.CREEPER) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.CREEPER);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            } else if (damager.getType() == EntityType.TNT) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.TNT);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            } else if (damager.getType() == EntityType.TNT_MINECART) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.TNT_MINECART);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            } else if (damager.getType() == EntityType.END_CRYSTAL) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.END_CRYSTAL);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            } else if (damager.getType() == EntityType.FIREBALL) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.FIREBALL);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            } else if (damager.getType() == EntityType.SMALL_FIREBALL) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.SMALL_FIREBALL);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            } else if (damager.getType() == EntityType.FIREWORK_ROCKET) { //
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, damager, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.FIREWORK_ROCKET);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getDamageSource().getDamageType() == DamageType.BAD_RESPAWN_POINT) {
                PlayerDamagedByExplosionEvent explosionEvent = new PlayerDamagedByExplosionEvent(player, null, event.getDamage(), PlayerDamagedByExplosionEvent.ExplosionDamageCause.BAD_RESPAWN_POINT);
                Bukkit.getPluginManager().callEvent(explosionEvent);
            }
        }
    }
}
