package io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.Entity.Entity.EntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.ItemStack.ItemBuilderAPI;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class LivingEntityStatistic extends EntityStatistic {

    private final LivingEntity livingEntity;

    // Constructor
    public LivingEntityStatistic(LivingEntity livingEntity) {
        super(livingEntity);
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
        double finalValue = 0;
        AttributeInstance attribute = this.livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            finalValue = attribute.getValue();
        }

        if (this.livingEntity.getEquipment() == null) {
            return finalValue;
        }

        ItemStack mainHand = this.livingEntity.getEquipment().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            return finalValue;
        }

        return new ItemBuilderAPI(mainHand).getHealth() + finalValue;
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

    public double getMissingHealthPercent() {
        return (getMaxHealth() - getHealth()) / getMaxHealth() * 100;
    }

    // Defense
    public double getDefense() {
        return getStatValue("DEFENSE","CombatStats", Statistic.CombatStats.DEFENSE.getBaseValue());
    }

    public void setDefense(double value) {
        setStatValue("DEFENSE","CombatStats", value, Statistic.CombatStats.DEFENSE.getMaxValue());
    }

    public double getSpeed() {
        return getStatValue("SPEED","MiscStats", Statistic.MiscStats.SPEED.getBaseValue());
    }

    public void setSpeed(double value) {
        setStatValue("SPEED","MiscStats", value, Statistic.MiscStats.SPEED.getMaxValue());
        AttributeInstance attribute = this.livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute != null) attribute.setBaseValue(0.10000000149011612 * (getSpeed() / 100));
    }

    @Override
    public double getDamage() {
        return getStatValue("DAMAGE","OtherStats", Statistic.OtherStats.DAMAGE.getBaseValue());
    }

    public String getDisplayName() {
        return this.livingEntity.getCustomName() == null? this.livingEntity.getName() : this.livingEntity.getCustomName();
    }

    @Override
    public String toString() {
        Map<String, Object> data = Map.of(
                this.livingEntity.toString(), Map.of(
                        "damage", getDamage(),
                        "health", getMaxHealth(),
                        "defense", getDefense(),
                        "speed", getSpeed())
        );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    // Utility Methods
    private double getStatValue(String key, String category, double baseValue) {
        double finalValue = NBT.modifyPersistentData(this.livingEntity, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats").getOrCreateCompound(category);
            if (!stats.hasTag(key)) {
                stats.setDouble(key, baseValue);
            }
            return stats.getDouble(key);
        });

        if (key.equals("SPEED")) {
            int venomousLevel = NBT.modifyPersistentData(this.livingEntity, nbt -> {
                ReadWriteNBT venomous = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantments");
                return venomous.getOrDefault(Enchantment.VENOMOUS.name() + "_EFFECT", 0);
            });
            double multi = venomousLevel * 5;
            finalValue = finalValue - finalValue * multi / 100;
        }

        if (this.livingEntity.getEquipment() == null) {
            return finalValue;
        }

        ItemStack mainHand = this.livingEntity.getEquipment().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            return finalValue;
        }

        ItemBuilderAPI itemBuilderAPI = new ItemBuilderAPI(mainHand);
        return switch (key) {
            case "DAMAGE" -> {
                double value = itemBuilderAPI.getDamage();
                if (itemBuilderAPI.hasEnchantment(Enchantment.TABASCO)) {
                    value += itemBuilderAPI.getEnchantmentLevel(Enchantment.TABASCO);
                }
                yield value + finalValue;
            }
            case "DEFENSE" -> itemBuilderAPI.getDefense() + finalValue;
            case "SPEED" -> itemBuilderAPI.getWalkSpeed() + finalValue;
            default -> finalValue;
        };
    }

    private void setStatValue(String key, String category, double value, double maxValue) {
        NBT.modifyPersistentData(this.livingEntity, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats").getOrCreateCompound(category);
            stats.setDouble(key, clamp(value, maxValue));
        });
    }

    private double clamp(double value, double maxValue) {
        return maxValue == -1 ? value : Math.min(value, maxValue);
    }
}
