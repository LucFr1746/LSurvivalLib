package io.github.lucfr1746.LSurvivalLib.Entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatistic extends LivingEntityStatistic {

    private final Player player;

    public PlayerStatistic(Player player) {
        super(player);
        this.player = player;
    }

    public double getStrength() {
        return getStat("STRENGTH", Statistic.CombatStats.STRENGTH.getBaseValue());
    }

    public void setStrength(double value) {
        setStat("STRENGTH", value, Statistic.CombatStats.STRENGTH.getMaxValue());
    }

    public double getIntelligence() {
        return getStat("INTELLIGENCE", Statistic.CombatStats.INTELLIGENCE.getBaseValue());
    }

    public void setIntelligence(double value) {
        double oldManaPool = getManaPool();
        setStat("INTELLIGENCE", value, Statistic.CombatStats.INTELLIGENCE.getMaxValue());
        if (getMana() == oldManaPool || getMana() > value) {
            setStat("MANA", value + 100, getManaPool());
        }
    }

    public double getCritChance() {
        return getStat("CRIT_CHANCE", Statistic.CombatStats.CRIT_CHANCE.getBaseValue());
    }

    public void setCritChance(double value) {
        setStat("CRIT_CHANCE", value, Statistic.CombatStats.CRIT_CHANCE.getMaxValue());
    }

    public double getCritDamage() {
        return getStat("CRIT_DAMAGE", Statistic.CombatStats.CRIT_DAMAGE.getBaseValue());
    }

    public void setCritDamage(double value) {
        setStat("CRIT_DAMAGE", value, Statistic.CombatStats.CRIT_DAMAGE.getMaxValue());
    }

    public double getBonusAttackSpeed() {
        return getStat("BONUS_ATTACK_SPEED", Statistic.CombatStats.BONUS_ATTACK_SPEED.getBaseValue());
    }

    public void setBonusAttackSpeed(double value) {
        setStat("BONUS_ATTACK_SPEED", value, Statistic.CombatStats.BONUS_ATTACK_SPEED.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute != null)  {
            double modified = 4 * (1 + getBonusAttackSpeed() / 100);
            attribute.setBaseValue(modified);
        }
    }

    public double getTrueDefense() {
        return getStat("TRUE_DEFENSE", Statistic.CombatStats.TRUE_DEFENSE.getBaseValue());
    }

    public void setTrueDefense(double value) {
        setStat("TRUE_DEFENSE", value, Statistic.CombatStats.TRUE_DEFENSE.getMaxValue());
    }

    public double getFerocity() {
        return getStat("FEROCITY", Statistic.CombatStats.FEROCITY.getBaseValue());
    }

    public void setFerocity(double value) {
        setStat("FEROCITY", value, Statistic.CombatStats.FEROCITY.getMaxValue());
    }

    public double getHealthRegen() {
        return getStat("HEALTH_REGEN", Statistic.CombatStats.HEALTH_REGEN.getBaseValue());
    }

    public void setHealthRegen(double value) {
        setStat("HEALTH_REGEN", value, Statistic.CombatStats.HEALTH_REGEN.getMaxValue());
    }

    public double getVitality() {
        return getStat("VITALITY", Statistic.CombatStats.VITALITY.getBaseValue());
    }

    public void setVitality(double value) {
        setStat("VITALITY", value, Statistic.CombatStats.VITALITY.getMaxValue());
    }

    public double getSwingRange() {
        return getStat("SWING_RANGE", Statistic.CombatStats.SWING_RANGE.getBaseValue());
    }

    public void setSwingRange(double value) {
        setStat("SWING_RANGE", value, Statistic.CombatStats.SWING_RANGE.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
        if (attribute != null) attribute.setBaseValue(getSwingRange());
    }

    public double getSpeed() {
        return getStat("SPEED", Statistic.MiscStats.SPEED.getBaseValue());
    }

    public void setSpeed(double value) {
        setStat("SPEED", value, Statistic.MiscStats.SPEED.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute != null) attribute.setBaseValue(0.10000000149011612 * (getSpeed() / 100));
    }

    public double getAbsorption() {
        return getStat("ABSORPTION", Statistic.OtherStats.ABSORPTION.getBaseValue());
    }

    public void setAbsorption(double value) {
        setStat("ABSORPTION", value, Statistic.OtherStats.ABSORPTION.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION);
        if (attribute != null) attribute.setBaseValue(getAbsorption());
        if (this.player instanceof Damageable damageable) damageable.setAbsorptionAmount(getAbsorption());
    }

    public double getMana() {
        return getStat("MANA", Statistic.CombatStats.INTELLIGENCE.getBaseValue() + 100);
    }

    public void setMana(double value) {
        setStat("MANA", value, getManaPool());
    }

    public double getManaPool() {
        return 100 + getIntelligence();
    }

    public double getTrueDamage() {
        return getStat("TRUE_DAMAGE", Statistic.OtherStats.TRUE_DAMAGE.getBaseValue());
    }

    public void setTrueDamage(double value) {
        setStat("TRUE_DAMAGE", value, Statistic.OtherStats.TRUE_DAMAGE.getMaxValue());
    }

    @Override
    public String toString() {
        // Creating the CombatStats map
        Map<String, Object> combatStats = new HashMap<>();
                            combatStats.put("health", getMaxHealth());
                            combatStats.put("defense", getDefense());
                            combatStats.put("strength", getStrength());
                            combatStats.put("intelligence", getIntelligence());
                            combatStats.put("critChance", getCritChance());
                            combatStats.put("critDamage", getCritDamage());
                            combatStats.put("bonusAttackSpeed", getBonusAttackSpeed());
                            combatStats.put("trueDefense", getTrueDefense());
                            combatStats.put("ferocity", getFerocity());
                            combatStats.put("healthRegen", getHealthRegen());
                            combatStats.put("vitality", getVitality());
                            combatStats.put("swingRange", getSwingRange());

        // Creating the MiscStats map
        Map<String, Object> miscStats = new HashMap<>();
                            miscStats.put("speed", getSpeed());

        // Creating the OtherStats map
        Map<String, Object> otherStats = new HashMap<>();
                            otherStats.put("absorption", getAbsorption());
                            otherStats.put("damage", getDamage());
                            otherStats.put("mana", getMana());
                            otherStats.put("trueDamage", getTrueDamage());

        // Combining all the stats into a final map
        Map<String, Object> data = Map.of(
                this.player.getName(), Map.of(
                        "CombatStats", combatStats,
                        "MiscStats", miscStats,
                        "OtherStats", otherStats)
        );

        // Converting the map to a JSON string using Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    private double getStat(String key, double defaultValue) {
        return NBT.modifyPersistentData(this.player, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats");
            if (!stats.hasTag(key)) {
                stats.setDouble(key, defaultValue);
            }
            return stats.getDouble(key);
        });
    }

    private void setStat(String key, double value, double maxValue) {
        NBT.modifyPersistentData(this.player, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats");
            stats.setDouble(key, maxValue == -1 ? value : Math.min(value, maxValue));
        });
    }
}