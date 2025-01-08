package io.github.lucfr1746.LSurvivalLib.Entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.ItemStack.ItemBuilderAPI;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatistic extends LivingEntityStatistic {

    private final Player player;

    public PlayerStatistic(Player player) {
        super(player);
        this.player = player;
    }

    public double getStrength() {
        return getStat("STRENGTH","CombatStats", Statistic.CombatStats.STRENGTH.getBaseValue());
    }

    public void setStrength(double value) {
        setStat("STRENGTH","CombatStats", value, Statistic.CombatStats.STRENGTH.getMaxValue());
    }

    public double getIntelligence() {
        return getStat("INTELLIGENCE","CombatStats", Statistic.CombatStats.INTELLIGENCE.getBaseValue());
    }

    public void setIntelligence(double value) {
        double oldManaPool = getManaPool();
        setStat("INTELLIGENCE","CombatStats", value, Statistic.CombatStats.INTELLIGENCE.getMaxValue());
        if (getMana() == oldManaPool || getMana() > value) {
            setStat("MANA","OtherStats", value + 100, getManaPool());
        }
    }

    public double getCritChance() {
        return getStat("CRIT_CHANCE","CombatStats", Statistic.CombatStats.CRIT_CHANCE.getBaseValue());
    }

    public void setCritChance(double value) {
        setStat("CRIT_CHANCE","CombatStats", value, Statistic.CombatStats.CRIT_CHANCE.getMaxValue());
    }

    public double getCritDamage() {
        return getStat("CRIT_DAMAGE","CombatStats", Statistic.CombatStats.CRIT_DAMAGE.getBaseValue());
    }

    public void setCritDamage(double value) {
        setStat("CRIT_DAMAGE","CombatStats", value, Statistic.CombatStats.CRIT_DAMAGE.getMaxValue());
    }

    public double getBonusAttackSpeed() {
        return getStat("BONUS_ATTACK_SPEED","CombatStats", Statistic.CombatStats.BONUS_ATTACK_SPEED.getBaseValue());
    }

    public void setBonusAttackSpeed(double value) {
        setStat("BONUS_ATTACK_SPEED","CombatStats", value, Statistic.CombatStats.BONUS_ATTACK_SPEED.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute != null)  {
            double modified = 4 * (1 + getBonusAttackSpeed() / 100);
            attribute.setBaseValue(modified);
        }
    }

    public double getTrueDefense() {
        return getStat("TRUE_DEFENSE","CombatStats", Statistic.CombatStats.TRUE_DEFENSE.getBaseValue());
    }

    public void setTrueDefense(double value) {
        setStat("TRUE_DEFENSE","CombatStats", value, Statistic.CombatStats.TRUE_DEFENSE.getMaxValue());
    }

    public double getFerocity() {
        return getStat("FEROCITY","CombatStats", Statistic.CombatStats.FEROCITY.getBaseValue());
    }

    public void setFerocity(double value) {
        setStat("FEROCITY","CombatStats", value, Statistic.CombatStats.FEROCITY.getMaxValue());
    }

    public double getHealthRegen() {
        return getStat("HEALTH_REGEN","CombatStats", Statistic.CombatStats.HEALTH_REGEN.getBaseValue());
    }

    public void setHealthRegen(double value) {
        setStat("HEALTH_REGEN","CombatStats", value, Statistic.CombatStats.HEALTH_REGEN.getMaxValue());
    }

    public double getVitality() {
        return getStat("VITALITY","CombatStats", Statistic.CombatStats.VITALITY.getBaseValue());
    }

    public void setVitality(double value) {
        setStat("VITALITY","CombatStats", value, Statistic.CombatStats.VITALITY.getMaxValue());
    }

    public double getSwingRange() {
        return getStat("SWING_RANGE","CombatStats", Statistic.CombatStats.SWING_RANGE.getBaseValue());
    }

    public void setSwingRange(double value) {
        setStat("SWING_RANGE","CombatStats", value, Statistic.CombatStats.SWING_RANGE.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE);
        if (attribute != null) attribute.setBaseValue(getSwingRange());
    }

    public double getMiningSpeed() {
        return getStat("MINING_SPEED","GatheringStats", Statistic.GatheringStats.MINING_SPEED.getBaseValue());
    }

    public void setMiningSpeed(double value) {
        setStat("MINING_SPEED","GatheringStats", value, Statistic.GatheringStats.MINING_SPEED.getMaxValue());
    }

    public double getMiningFortune() {
        return getStat("MINING_FORTUNE","GatheringStats", Statistic.GatheringStats.MINING_FORTUNE.getBaseValue());
    }

    public void setMiningFortune(double value) {
        setStat("MINING_FORTUNE","GatheringStats", value, Statistic.GatheringStats.MINING_FORTUNE.getMaxValue());
    }

    public double getMiningSpread() {
        return getStat("MINING_SPREAD","GatheringStats", Statistic.GatheringStats.MINING_SPREAD.getBaseValue());
    }

    public void setMiningSpread(double value) {
        setStat("MINING_SPREAD","GatheringStats", value, Statistic.GatheringStats.MINING_SPREAD.getMaxValue());
    }

    public double getFarmingFortune() {
        return getStat("FARMING_FORTUNE","GatheringStats", Statistic.GatheringStats.FARMING_FORTUNE.getBaseValue());
    }

    public void setFarmingFortune(double value) {
        setStat("FARMING_FORTUNE","GatheringStats", value, Statistic.GatheringStats.FARMING_FORTUNE.getMaxValue());
    }

    public double getForagingFortune() {
        return getStat("FORAGING_FORTUNE","GatheringStats", Statistic.GatheringStats.FORAGING_FORTUNE.getBaseValue());
    }

    public void setForagingFortune(double value) {
        setStat("FORAGING_FORTUNE","GatheringStats", value, Statistic.GatheringStats.FORAGING_FORTUNE.getMaxValue());
    }

    public double getAlchemyWisdom() {
        return getStat("ALCHEMY_WISDOM","WisdomStats", Statistic.WisdomStats.ALCHEMY_WISDOM.getBaseValue());
    }

    public void setAlchemyWisdom(double value) {
        setStat("ALCHEMY_WISDOM","WisdomStats", value, Statistic.WisdomStats.ALCHEMY_WISDOM.getMaxValue());
    }

    public double getCarpentryWisdom() {
        return getStat("CARPENTRY_WISDOM","WisdomStats", Statistic.WisdomStats.CARPENTRY_WISDOM.getBaseValue());
    }

    public void setCarpentryWisdom(double value) {
        setStat("CARPENTRY_WISDOM","WisdomStats", value, Statistic.WisdomStats.CARPENTRY_WISDOM.getMaxValue());
    }

    public double getCombatWisdom() {
        return getStat("COMBAT_WISDOM","WisdomStats", Statistic.WisdomStats.COMBAT_WISDOM.getBaseValue());
    }

    public void setCombatWisdom(double value) {
        setStat("COMBAT_WISDOM","WisdomStats", value, Statistic.WisdomStats.COMBAT_WISDOM.getMaxValue());
    }

    public double getEnchantingWisdom() {
        return getStat("ENCHANTING_WISDOM","WisdomStats", Statistic.WisdomStats.ENCHANTING_WISDOM.getBaseValue());
    }

    public void setEnchantingWisdom(double value) {
        setStat("ENCHANTING_WISDOM","WisdomStats", value, Statistic.WisdomStats.ENCHANTING_WISDOM.getMaxValue());
    }

    public double getFarmingWisdom() {
        return getStat("FARMING_WISDOM","WisdomStats", Statistic.WisdomStats.FARMING_WISDOM.getBaseValue());
    }

    public void setFarmingWisdom(double value) {
        setStat("FARMING_WISDOM","WisdomStats", value, Statistic.WisdomStats.FARMING_WISDOM.getMaxValue());
    }

    public double getFishingWisdom() {
        return getStat("FISHING_WISDOM","WisdomStats", Statistic.WisdomStats.FISHING_WISDOM.getBaseValue());
    }

    public void setFishingWisdom(double value) {
        setStat("FISHING_WISDOM","WisdomStats", value, Statistic.WisdomStats.FISHING_WISDOM.getMaxValue());
    }

    public double getForagingWisdom() {
        return getStat("FORAGING_WISDOM","WisdomStats", Statistic.WisdomStats.FORAGING_WISDOM.getBaseValue());
    }

    public void setForagingWisdom(double value) {
        setStat("FORAGING_WISDOM","WisdomStats", value, Statistic.WisdomStats.FORAGING_WISDOM.getMaxValue());
    }

    public double getMiningWisdom() {
        return getStat("MINING_WISDOM","WisdomStats", Statistic.WisdomStats.MINING_WISDOM.getBaseValue());
    }

    public void setMiningWisdom(double value) {
        setStat("MINING_WISDOM","WisdomStats", value, Statistic.WisdomStats.MINING_WISDOM.getMaxValue());
    }

    public double getMagicFind() {
        return getStat("MAGIC_FIND","MiscStats", Statistic.MiscStats.MAGIC_FIND.getBaseValue());
    }

    public void setMagicFind(double value) {
        setStat("MAGIC_FIND","MiscStats", value, Statistic.MiscStats.MAGIC_FIND.getMaxValue());
    }

    public double getAbsorption() {
        return getStat("ABSORPTION","OtherStats", Statistic.OtherStats.ABSORPTION.getBaseValue());
    }

    public void setAbsorption(double value) {
        setStat("ABSORPTION","OtherStats", value, Statistic.OtherStats.ABSORPTION.getMaxValue());
        AttributeInstance attribute = this.player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION);
        if (attribute != null) attribute.setBaseValue(getAbsorption());
        if (this.player instanceof Damageable damageable) damageable.setAbsorptionAmount(getAbsorption());
    }

    public double getMana() {
        return getStat("MANA","OtherStats", Statistic.CombatStats.INTELLIGENCE.getBaseValue() + 100);
    }

    public void setMana(double value) {
        setStat("MANA","OtherStats", value, getManaPool());
    }

    public double getManaPool() {
        return 100 + getIntelligence();
    }

    public double getTrueDamage() {
        return getStat("TRUE_DAMAGE","OtherStats", Statistic.OtherStats.TRUE_DAMAGE.getBaseValue());
    }

    public void setTrueDamage(double value) {
        setStat("TRUE_DAMAGE","OtherStats", value, Statistic.OtherStats.TRUE_DAMAGE.getMaxValue());
    }

    @Override
    public String getDisplayName() {
        return this.player.getDisplayName();
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

        // Creating the GatheringStats map
        Map<String, Object> gatheringStats = new HashMap<>();
                            gatheringStats.put("miningSpeed", getMiningSpeed());
                            gatheringStats.put("miningFortune", getMiningFortune());
                            gatheringStats.put("miningSpread", getMiningSpread());
                            gatheringStats.put("farmingFortune", getFarmingFortune());
                            gatheringStats.put("foragingFortune", getForagingFortune());

        // Creating the GatheringStats map
        Map<String, Object> wisdomStats = new HashMap<>();
                            wisdomStats.put("alchemyWisdom", getAlchemyWisdom());
                            wisdomStats.put("carpentryWisdom", getCarpentryWisdom());
                            wisdomStats.put("combatWisdom", getCombatWisdom());
                            wisdomStats.put("enchantingWisdom", getEnchantingWisdom());
                            wisdomStats.put("farmingWisdom", getFarmingWisdom());
                            wisdomStats.put("fishingWisdom", getFishingWisdom());
                            wisdomStats.put("foragingWisdom", getForagingWisdom());
                            wisdomStats.put("miningWisdom", getMiningWisdom());

        // Creating the MiscStats map
        Map<String, Object> miscStats = new HashMap<>();
                            miscStats.put("speed", getSpeed());
                            miscStats.put("magicFind", getMagicFind());

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
                        "GatheringStats", gatheringStats,
                        "WisdomStats", wisdomStats,
                        "MiscStats", miscStats,
                        "OtherStats", otherStats)
        );

        // Converting the map to a JSON string using Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    private double getStat(String key, String category, double defaultValue) {
        double finalValue =  NBT.modifyPersistentData(this.player, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats").getOrCreateCompound(category);
            if (!stats.hasTag(key)) {
                stats.setDouble(key, defaultValue);
            }
            return stats.getDouble(key);
        });

        if (this.player.getEquipment() == null) {
            return finalValue;
        }

        ItemStack mainHand = this.player.getEquipment().getItemInMainHand();
        if (mainHand.getType() == Material.AIR) {
            return finalValue;
        }

        ItemBuilderAPI itemBuilderAPI = new ItemBuilderAPI(mainHand);
        return switch (key) {
            case "STRENGTH" -> itemBuilderAPI.getStrength() + finalValue;
            case "INTELLIGENCE" -> itemBuilderAPI.getIntelligence() + finalValue;
            case "CRIT_CHANCE" -> itemBuilderAPI.getCritChance() + finalValue;
            case "CRIT_DAMAGE" -> itemBuilderAPI.getCritDamage() + finalValue;
            case "BONUS_ATTACK_SPEED" -> itemBuilderAPI.getBonusAttackSpeed() + finalValue;
            case "TRUE_DEFENSE" -> itemBuilderAPI.getTrueDefense() + finalValue;
            case "FEROCITY" -> {
                double value = itemBuilderAPI.getFerocity();
                if (itemBuilderAPI.hasEnchantment(Enchantment.VICIOUS)) {
                    value += itemBuilderAPI.getEnchantmentLevel(Enchantment.VICIOUS);
                }
                yield value + finalValue;
            }
            case "HEALTH_REGEN" -> itemBuilderAPI.getHealthRegen() + finalValue;
            case "VITALITY" -> itemBuilderAPI.getVitality() + finalValue;
            case "SWING_RANGE" -> itemBuilderAPI.getSwingRange() + finalValue;

            case "MINING_SPEED" -> itemBuilderAPI.getMiningSpeed() + finalValue;
            case "MINING_FORTUNE" -> itemBuilderAPI.getMiningFortune() + finalValue;
            case "MINING_SPREAD" -> itemBuilderAPI.getMiningSpread() + finalValue;
            case "FARMING_FORTUNE" -> itemBuilderAPI.getFarmingFortune() + finalValue;
            case "FORAGING_FORTUNE" -> itemBuilderAPI.getForagingFortune() + finalValue;

            case "ALCHEMY_WISDOM" -> itemBuilderAPI.getAlchemyWisdom() + finalValue;
            case "CARPENTRY_WISDOM" -> itemBuilderAPI.getCarpentryWisdom() + finalValue;
            case "COMBAT_WISDOM" -> itemBuilderAPI.getCombatWisdom() + finalValue;
            case "ENCHANTING_WISDOM" -> itemBuilderAPI.getEnchantingWisdom() + finalValue;
            case "FARMING_WISDOM" -> itemBuilderAPI.getFarmingWisdom() + finalValue;
            case "FISHING_WISDOM" -> itemBuilderAPI.getFishingWisdom() + finalValue;
            case "FORAGING_WISDOM" -> itemBuilderAPI.getForagingWisdom() + finalValue;
            case "MINING_WISDOM" -> itemBuilderAPI.getMiningWisdom() + finalValue;

            case "MAGIC_FIND" -> {
                double value = itemBuilderAPI.getMagicFind();
                if (itemBuilderAPI.hasEnchantment(Enchantment.DIVINE_GIFT)) {
                    value += itemBuilderAPI.getEnchantmentLevel(Enchantment.DIVINE_GIFT) * 2;
                }
                yield value + finalValue;
            }

            case "ABSORPTION" -> itemBuilderAPI.getAbsorption() + finalValue;
            case "TRUE_DAMAGE" -> itemBuilderAPI.getTrueDamage() + finalValue;
            default -> finalValue;
        };
    }

    private void setStat(String key, String category, double value, double maxValue) {
        NBT.modifyPersistentData(this.player, nbt -> {
            ReadWriteNBT stats = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("Stats").getOrCreateCompound(category);
            stats.setDouble(key, maxValue == -1 ? value : Math.min(value, maxValue));
        });
    }
}