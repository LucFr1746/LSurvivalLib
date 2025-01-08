package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamagedByContactEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class PlayerContactListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerContactListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamagedByContactEvent(PlayerDamagedByContactEvent event) {
        switch (event.getContactDamageCause()) {
            case CACTUS, BERRY_BUSH -> event.setDamage(4);
            case STING, SUFFOCATION -> event.setDamage(10);
            case DRIPSTONE -> event.setDamage(((event.getPlayer().getFallDistance() * 2 - 4) * 200/33));
            case FALLING_DRIPSTONE, FALLING_BLOCK, FALLING_ANVIL, FLY_INTO_WALL -> event.setDamage(event.getDamage() * 200/33);
            case OUTSIDE_BORDER -> {
                PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You went out of world's border.", true);
                Bukkit.getPluginManager().callEvent(deathEvent);
                return;
            }
        }
        if (event.getDamage() <= 0) return;
        if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
            switch (event.getContactDamageCause()) {
                case CACTUS -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were pricked to death by a cactus.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case BERRY_BUSH -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were pricked to death by a berry bush.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case STING -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were stung to death by a bee.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case DRIPSTONE -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were stabbed to death by a dripstone.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case FALLING_DRIPSTONE -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were hit in the head by a small falling dripstone.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case THORNS -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were pricked by sharp thorns.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case FALLING_ANVIL -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were hit in the head by a small falling anvil.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case FALLING_BLOCK -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You were hit in the head by a small falling block.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case FLY_INTO_WALL -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You banged your head against the wall.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
                case SUFFOCATION -> {
                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(event.getPlayer(), " &c☠ &7You stuck in the wall.", true);
                    Bukkit.getPluginManager().callEvent(deathEvent);
                }
            }
        } else {
            event.getPlayer().damage(event.getDamage());
            new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.CONTACT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerContact(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getDamageSource().getDamageType() == DamageType.STALAGMITE) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.DRIPSTONE);
            Bukkit.getPluginManager().callEvent(contactEvent);
        } else if (event.getDamageSource().getDamageType() == DamageType.STING) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.STING);
            Bukkit.getPluginManager().callEvent(contactEvent);
        } else if (event.getDamageSource().getDamageType() == DamageType.FALLING_ANVIL) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.FALLING_ANVIL);
            Bukkit.getPluginManager().callEvent(contactEvent);
        } else if (event.getDamageSource().getDamageType() == DamageType.FALLING_BLOCK) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.FALLING_BLOCK);
            Bukkit.getPluginManager().callEvent(contactEvent);
        } else if (event.getDamageSource().getDamageType() == DamageType.FALLING_STALACTITE) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.FALLING_DRIPSTONE);
            Bukkit.getPluginManager().callEvent(contactEvent);
        } else if (event.getDamageSource().getDamageType() == DamageType.FLY_INTO_WALL) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.FLY_INTO_WALL);
            Bukkit.getPluginManager().callEvent(contactEvent);
        } else if (event.getDamageSource().getDamageType() == DamageType.THORNS) {
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.THORNS);
            Bukkit.getPluginManager().callEvent(contactEvent);
        }
    }

    private final Set<Player> cactusCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCactusPricked(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getDamageSource().getDamageType() == DamageType.CACTUS) {
            event.setCancelled(true);
            if (!cactusCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    cactusCooldownSet.remove(player);
                }
            }.runTaskLater(this.plugin, 10);
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.CACTUS);
            Bukkit.getPluginManager().callEvent(contactEvent);
        }
    }

    private final Set<Player> berryCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBerryPricked(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getDamageSource().getDamageType() == DamageType.SWEET_BERRY_BUSH) {
            event.setCancelled(true);
            if (!berryCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    berryCooldownSet.remove(player);
                }
            }.runTaskLater(this.plugin, 10);
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.BERRY_BUSH);
            Bukkit.getPluginManager().callEvent(contactEvent);
        }
    }

    private final Set<Player> borderCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBorderPricked(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getDamageSource().getDamageType() == DamageType.OUTSIDE_BORDER) {
            event.setCancelled(true);
            if (!borderCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    borderCooldownSet.remove(player);
                }
            }.runTaskLater(this.plugin, 10);
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.OUTSIDE_BORDER);
            Bukkit.getPluginManager().callEvent(contactEvent);
        }
    }

    private final Set<Player> suffocationCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSuffocation(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            event.setCancelled(true);
            if (!suffocationCooldownSet.add(player)) {
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    suffocationCooldownSet.remove(player);
                }
            }.runTaskLater(this.plugin, 10);
            PlayerDamagedByContactEvent contactEvent = new PlayerDamagedByContactEvent(player, event.getDamage(), PlayerDamagedByContactEvent.ContactDamageCause.SUFFOCATION);
            Bukkit.getPluginManager().callEvent(contactEvent);
        }
    }
}
