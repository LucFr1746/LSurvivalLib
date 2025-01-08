package io.github.lucfr1746.LSurvivalLib.Utils.APIs;

import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.Objects;

public class DamageIndicatorAPI {

    public enum DamageCause {
        FIRE, FELL, DROWNING, CONTACT, EXPLOSION, NORMAL, CRITICAL, LIGHTNING;
    }

    public DamageIndicatorAPI(LSurvivalLib plugin, double damage, Location location, DamageCause damageCause) {
        double randomX = Math.random();
        double randomY = Math.random();
        double randomZ = Math.random();
        randomX -= 0.5D;
        randomY += 0.25D;
        randomZ -= 0.5D;
        Objects.requireNonNull(location.getWorld()).spawn(location.clone().add(randomX, randomY, randomZ), ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setMarker(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(getDamageFormat(damage, damageCause));
            Bukkit.getScheduler().runTaskLater(plugin, armorStand::remove, 30L);
        });
    }

    private String getDamageFormat(double damage, DamageCause damageCause) {
        return switch (damageCause) {
            case FIRE -> ChatColor.GOLD + NumberAPI.toStringFixed(damage, 0);
            case DROWNING -> ChatColor.DARK_AQUA + NumberAPI.toStringFixed(damage, 0);
            case LIGHTNING -> ChatColor.YELLOW + NumberAPI.toStringFixed(damage, 0);
            case CRITICAL -> {
                ChatColor[] colors = {
                        ChatColor.WHITE,
                        ChatColor.WHITE,
                        ChatColor.YELLOW,
                        ChatColor.GOLD,
                        ChatColor.RED,
                        ChatColor.RED,
                };
                String finalDamage = "✧" + NumberAPI.toStringFixed(damage, 0) + "✧";
                StringBuilder coloredText = new StringBuilder();

                for (int i = 0; i < finalDamage.length(); i++) {
                    ChatColor color = colors[i % colors.length];
                    coloredText.append(color).append(finalDamage.charAt(i));
                }
                yield coloredText.toString();
            }
            default -> ChatColor.GRAY + NumberAPI.toStringFixed(damage, 0);
        };
    }
}
