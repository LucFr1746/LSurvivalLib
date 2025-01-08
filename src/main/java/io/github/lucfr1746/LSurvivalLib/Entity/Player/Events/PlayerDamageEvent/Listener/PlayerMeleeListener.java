package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamagedByMeleeEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatistic;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Category.Category;
import io.github.lucfr1746.LSurvivalLib.ItemStack.ItemBuilderAPI;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerMeleeListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerMeleeListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
//
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerDamagedByMeleePlayer(PlayerDamagedByMeleeEvent event) {
//        if (!(event.getAttacker() instanceof Player attacker)) return;
//
//        PlayerStatistic attackerStats = new PlayerStatistic(attacker);
//        int extraAttacks = (int) Math.floor(attackerStats.getFerocity() / 100);
//        if (ThreadLocalRandom.current().nextDouble() <= (attackerStats.getFerocity() % 100) / 100) extraAttacks++;
//
//        int finalExtraAttacks = extraAttacks;
//        final float attackCooldown = attacker.getAttackCooldown();
//        new BukkitRunnable() {
//            private int attackCount = 0;
//
//            @Override
//            public void run() {
//                if (attackCount > finalExtraAttacks) {
//                    cancel();
//                    return;
//                }
//
//                ItemBuilderAPI itemBuilderAPI = new ItemBuilderAPI(attacker.getInventory().getItemInMainHand());
//                boolean isBow = itemBuilderAPI.getCategory() == Category.BOW || itemBuilderAPI.getCategory() == Category.CROSS_BOW;
//
//                double damage = (isBow ? attackerStats.getDamage() - itemBuilderAPI.getDamage() : attackerStats.getDamage())
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
//                PlayerStatistic playerStats = new PlayerStatistic(event.getPlayer());
//                damage *= (1 - playerStats.getDefense() / (playerStats.getDefense() + 100));
//
//                damage *= attackCooldown;
//
//                event.setDamage(damage);
//
//                if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
//                    PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(
//                            event.getPlayer(),
//                            " &c☠ &7You were killed by " + ChatColor.stripColor(attacker.getDisplayName()) + ".",
//                            true
//                    );
//                    Bukkit.getPluginManager().callEvent(deathEvent);
//                    cancel();
//                } else {
//                    event.getPlayer().damage(event.getDamage());
//                    new DamageIndicatorAPI(plugin, event.getDamage(), event.getPlayer().getLocation(),
//                            isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);
//                }
//                attackCount++;
//            }
//        }.runTaskTimer(this.plugin, 0L, 10L);
//    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamagedByMelee(PlayerDamagedByMeleeEvent event) {
        if (event.getAttacker() instanceof Player) return;

        LivingEntityStatistic attackerStats = new LivingEntityStatistic(event.getAttacker());
        double damage = attackerStats.getDamage();

        PlayerStatistic playerStats = new PlayerStatistic(event.getPlayer());
        damage *= (1 - playerStats.getDefense() / (playerStats.getDefense() + 100));

        event.setDamage(damage);

        if (event.getPlayer().getHealth() - event.getDamage() <= 0) {
            PlayerGoingToDeathEvent deathEvent = new PlayerGoingToDeathEvent(
                    event.getPlayer(),
                    " &c☠ &7You were killed by " + ChatColor.stripColor(attackerStats.getDisplayName()) + ".",
                    true
            );
            Bukkit.getPluginManager().callEvent(deathEvent);
        } else {
            event.getPlayer().damage(event.getDamage());
            new DamageIndicatorAPI(this.plugin, event.getDamage(), event.getPlayer().getLocation(), DamageIndicatorAPI.DamageCause.NORMAL);
        }
    }

    private final Set<Player> meleeCooldownSet = new HashSet<>();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamagedByMeleeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;

        event.setCancelled(true);
        if (!meleeCooldownSet.add(player)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                meleeCooldownSet.remove(player);
            }
        }.runTaskLater(this.plugin, 10);
        PlayerDamagedByMeleeEvent meleeEvent = new PlayerDamagedByMeleeEvent(player, attacker, event.getDamage());
        Bukkit.getPluginManager().callEvent(meleeEvent);
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
