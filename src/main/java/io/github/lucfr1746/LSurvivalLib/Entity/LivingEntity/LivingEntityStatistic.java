package io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.ItemStack.ItemBuilderAPI;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LivingEntityStatistic {

    private final LivingEntity livingEntity;

    // Constructor
    public LivingEntityStatistic(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    // Health
    public double getHealth() {
        return this.livingEntity.getHealth();
    }

    public void setHealth(double value) {
        double maxValue = getMaxHealth();
        this.livingEntity.setHealth(clamp(value, maxValue));
    }

    public double getMaxHealth() {
        AttributeInstance attribute = this.livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            return attribute.getValue();
        }
        return Statistic.CombatStats.HEALTH.getMaxValue() == -1
                ? Double.MAX_VALUE
                : Statistic.CombatStats.HEALTH.getMaxValue();
    }

    public void setMaxHealth(double value) {
        AttributeInstance attribute = this.livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            double maxValue = Statistic.CombatStats.HEALTH.getMaxValue();
            value = clamp(value, maxValue);

            double oldMaxHealth = attribute.getValue();
            attribute.setBaseValue(value);
            if (getHealth() == oldMaxHealth || getHealth() > value) {
                setHealth(value);
            }
        }
        if (this.livingEntity instanceof Player player) {
            player.setHealthScaled(true);
            double maxHealth = getMaxHealth();
            if (maxHealth <= 100) {
                player.setHealthScale(20);
            } else if (maxHealth <= 1300) {
                player.setHealthScale(20 + (maxHealth - 100) / 60);
            } else {
                player.setHealthScale(40);
            }
            if (getHealth() > maxHealth) {
                setHealth(maxHealth);
            }
        }
    }

    // Damage
    public double getDamage() {
        double entityDamage = getStatValue("DAMAGE", Statistic.OtherStats.DAMAGE.getBaseValue());

        if (this.livingEntity.getEquipment() == null) {
            return entityDamage;
        }

        ItemStack mainHand = this.livingEntity.getEquipment().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            return entityDamage;
        }

        return new ItemBuilderAPI(mainHand).getDamage() + entityDamage;
    }

    public void setDamage(double value) {
        setStatValue("DAMAGE", value, Statistic.OtherStats.DAMAGE.getMaxValue());
    }

    // Defense
    public double getDefense() {
        return getStatValue("DEFENSE", Statistic.CombatStats.DEFENSE.getBaseValue());
    }

    public void setDefense(double value) {
        setStatValue("DEFENSE", value, Statistic.CombatStats.DEFENSE.getMaxValue());
    }

    @Override
    public String toString() {
        Map<String, Object> data = Map.of(
                this.livingEntity.toString(), Map.of(
                        "health", getMaxHealth(),
                        "damage", getDamage(),
                        "defense", getDefense())
        );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    // Utility Methods
    private double getStatValue(String key, double baseValue) {
        return NBT.modifyPersistentData(this.livingEntity, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats");
            if (!stats.hasTag(key)) {
                stats.setDouble(key, baseValue);
            }
            return stats.getDouble(key);
        });
    }

    private void setStatValue(String key, double value, double maxValue) {
        NBT.modifyPersistentData(this.livingEntity, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats");
            stats.setDouble(key, clamp(value, maxValue));
        });
    }

    private double clamp(double value, double maxValue) {
        return maxValue == -1 ? value : Math.min(value, maxValue);
    }
}
