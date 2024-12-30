package io.github.lucfr1746.LSurvivalLib.Entity.Player;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.ArmorChangeEvent.Listener.ArmorChangeListener;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager implements Listener {

    private final LSurvivalLib plugin;
    private static PlayerActionBarManager actionBarManager;

    public PlayerManager(LSurvivalLib plugin) {
        this.plugin = plugin;
        new ArmorChangeListener(plugin);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static PlayerActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    private final HashMap<UUID, Integer> schedulerMap = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            PlayerStatistic playerStatistic = new PlayerStatistic(player);
            playerStatistic.setMaxHealth(Statistic.CombatStats.HEALTH.getBaseValue());
            playerStatistic.setDefense(Statistic.CombatStats.DEFENSE.getBaseValue());
            playerStatistic.setStrength(Statistic.CombatStats.STRENGTH.getBaseValue());
            playerStatistic.setIntelligence(Statistic.CombatStats.INTELLIGENCE.getBaseValue());
            playerStatistic.setCritChance(Statistic.CombatStats.CRIT_CHANCE.getBaseValue());
            playerStatistic.setCritDamage(Statistic.CombatStats.CRIT_DAMAGE.getBaseValue());
            playerStatistic.setBonusAttackSpeed(Statistic.CombatStats.BONUS_ATTACK_SPEED.getBaseValue());
            playerStatistic.setTrueDefense(Statistic.CombatStats.TRUE_DEFENSE.getBaseValue());
            playerStatistic.setFerocity(Statistic.CombatStats.FEROCITY.getBaseValue());
            playerStatistic.setHealthRegen(Statistic.CombatStats.HEALTH_REGEN.getBaseValue());
            playerStatistic.setVitality(Statistic.CombatStats.VITALITY.getBaseValue());
            playerStatistic.setSwingRange(Statistic.CombatStats.SWING_RANGE.getBaseValue());

            playerStatistic.setSpeed(Statistic.MiscStats.SPEED.getBaseValue());

            playerStatistic.setAbsorption(Statistic.OtherStats.ABSORPTION.getBaseValue());
            playerStatistic.setDamage(Statistic.OtherStats.DAMAGE.getBaseValue());
            playerStatistic.setTrueDamage(Statistic.OtherStats.TRUE_DAMAGE.getBaseValue());
        }
        this.registerPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.unRegisterPlayer(event.getPlayer());
    }

    @EventHandler
    public void onEntityRegen(EntityRegainHealthEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            e.setCancelled(true);
        }
    }

    private void registerPlayer(Player player) {
        actionBarManager = new PlayerActionBarManager(this.plugin, player);
        AtomicInteger tickCounter = new AtomicInteger();
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            PlayerStatistic playerStatistic = new PlayerStatistic(player);

            if (tickCounter.get() % 2 == 0) { // 40 ticks
                regenerateHealth(playerStatistic);
                regenerateMana(playerStatistic);
            }

            if (Statistic.getActionBar_playerStatsOverlay_enabled()) {
                String message = Statistic.getActionBar_playerStatsOverlay_message();
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    message = PlaceholderAPI.setPlaceholders(player, Statistic.getActionBar_playerStatsOverlay_message());
                }
                actionBarManager.addActionBarToQuery(MiniMessage.miniMessage().deserialize(message), Statistic.getActionBar_playerStatsOverlay_priority(), 1);
            }
            tickCounter.getAndIncrement();
        }, 0L, 20L);

        this.schedulerMap.put(player.getUniqueId(), taskId);
    }

    private void unRegisterPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        if (this.schedulerMap.containsKey(playerId)) {
            int taskId = this.schedulerMap.get(playerId);
            Bukkit.getScheduler().cancelTask(taskId);
            this.schedulerMap.remove(playerId);
        }
    }

    private void regenerateHealth(PlayerStatistic playerStatistic) {
        double maxHealth = playerStatistic.getMaxHealth();

        double totalHealthRegen = (1.5 + (maxHealth / 100) * (playerStatistic.getHealthRegen() / 100)) * (playerStatistic.getVitality() / 100);
        playerStatistic.setHealth(playerStatistic.getHealth() + totalHealthRegen);
    }

    private void regenerateMana(PlayerStatistic playerStatistic) {
        double manaPool = playerStatistic.getIntelligence() + playerStatistic.getMana();

        double totalManaRegen = 1.5 + (0.01 * manaPool);
        playerStatistic.setMana(playerStatistic.getMana() + totalManaRegen);
    }
}
