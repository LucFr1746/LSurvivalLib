package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerGoingToDeathEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerAPI;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatistic;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.NumberAPI;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;

public class PlayerDeathListener implements Listener {

    private final LSurvivalLib plugin;

    public PlayerDeathListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerGoingToDeathEvent event) {
        PlayerAPI playerAPI = new PlayerAPI(event.getPlayer());
        playerAPI.sendColoredMessage(event.getMessage());
        Location respawnLocation = event.getPlayer().getRespawnLocation() != null ? event.getPlayer().getRespawnLocation() : Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
        event.getPlayer().teleport(respawnLocation);
        PlayerStatistic playerStatistic = new PlayerStatistic(event.getPlayer());
        playerStatistic.setHealth(playerStatistic.getMaxHealth());
        event.getPlayer().setFoodLevel(20);
        event.getPlayer().setFireTicks(0);
        event.getPlayer().getActivePotionEffects().clear();
        if (event.isLostCoin()) {
            double takeAmount = LSurvivalLib.getEconomy().getBalance(event.getPlayer()) / 2;
            EconomyResponse r = LSurvivalLib.getEconomy().withdrawPlayer(event.getPlayer(), takeAmount);
            if(r.transactionSuccess()) {
                playerAPI.sendColoredMessage("&cYou died and lost " + NumberAPI.toStringFixed(takeAmount,1) + " coins!");
            } else {
                playerAPI.sendColoredMessage(String.format("An error occurred: %s\nPlease contact the admin!", r.errorMessage));
            }
            playerAPI.playSound(respawnLocation, Sound.BLOCK_ANVIL_LAND, 1, 2);
        } else {
            playerAPI.playSound(respawnLocation, Sound.ENTITY_PLAYER_DEATH, 1, 1);
        }
    }
}
