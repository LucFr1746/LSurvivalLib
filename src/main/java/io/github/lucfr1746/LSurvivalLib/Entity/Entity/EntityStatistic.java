package io.github.lucfr1746.LSurvivalLib.Entity.Entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import org.bukkit.entity.Entity;

import java.util.Map;

public class EntityStatistic {

    private final Entity entity;

    public EntityStatistic(Entity entity) {
        this.entity = entity;
    }

    // Damage
    public double getDamage() {
        return getStatValue("DAMAGE","OtherStats", Statistic.OtherStats.DAMAGE.getBaseValue());
    }

    public void setDamage(double value) {
        setStatValue("DAMAGE","OtherStats", value, Statistic.OtherStats.DAMAGE.getMaxValue());
    }

    @Override
    public String toString() {
        Map<String, Object> data = Map.of(
                this.entity.toString(), Map.of(
                        "damage", getDamage())
        );
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    // Utility Methods
    private double getStatValue(String key, String category, double baseValue) {
        return NBT.modifyPersistentData(this.entity, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats").getOrCreateCompound(category);
            if (!stats.hasTag(key)) {
                stats.setDouble(key, baseValue);
            }
            return stats.getDouble(key);
        });
    }

    private void setStatValue(String key, String category, double value, double maxValue) {
        NBT.modifyPersistentData(this.entity, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats").getOrCreateCompound(category);
            stats.setDouble(key, clamp(value, maxValue));
        });
    }

    private double clamp(double value, double maxValue) {
        return maxValue == -1 ? value : Math.min(value, maxValue);
    }
}
