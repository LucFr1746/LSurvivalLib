package io.github.lucfr1746.LSurvivalLib.Entity.Player;

import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.ColorAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.LoggerAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.NumberAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerStatisticExpansion extends PlaceholderExpansion {

    private final LSurvivalLib plugin;

    public PlayerStatisticExpansion(LSurvivalLib plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "lucfr1746";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lstats";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        List<String> sections = new ArrayList<>(Arrays.asList(params.split("_"))); // Use ArrayList
        if (sections.size() == 1) {
            sections.add(""); // Ensure at least two elements
        }
        if (sections.size() < 2) {
            return null; // Invalid format, must include at least "<stat>_<subtype>"
        }

        String stat = sections.get(0); // Use index 0 instead of getFirst()
        String subtype = sections.get(1); // Use index 1
        PlayerStatistic playerStatistic = new PlayerStatistic(player);

        return switch (subtype) {
            case "" -> NumberAPI.toStringFixed(getPlayerValue(playerStatistic, stat), 2);
            case "max" -> NumberAPI.toStringFixed(getPlayerMaxValue(playerStatistic, stat), 2);
            case "name" -> getStatisticName(stat);
            case "color" -> ColorAPI.colorToHex(getStatisticColor(stat));
            case "symbol" -> String.valueOf(getStatisticSymbol(stat));
            case "value" -> {
                if (sections.size() < 3) yield null;
                String type = sections.get(2);
                yield switch (type) {
                    case "base" -> NumberAPI.toStringFixed(getStatisticBaseValue(stat), 2);
                    case "max" -> NumberAPI.toStringFixed(getStatisticMaxValue(stat), 2);
                    default -> null;
                };
            }
            case "fixed" -> {
                if (sections.size() < 3) yield null; // <stat>_fixed_<take>
                else if (sections.size() == 3) sections.add(""); // <stat>_fixed_<take>_<sub>
                int take = NumberAPI.isInteger(sections.get(2)) ? Integer.parseInt(sections.get(2)) : 0;
                String sub = sections.get(3);

                yield switch (sub) {
                    case "" -> NumberAPI.toStringFixed(getPlayerValue(playerStatistic, stat), take);
                    case "max" -> NumberAPI.toStringFixed(getPlayerMaxValue(playerStatistic, stat), take);
                    case "value" -> {
                        if (sections.size() < 5) yield null;
                        String type = sections.get(4);
                        yield switch (type) {
                            case "base" -> NumberAPI.toStringFixed(getStatisticBaseValue(stat), take);
                            case "max" -> NumberAPI.toStringFixed(getStatisticMaxValue(stat), take);
                            default -> null;
                        };
                    }
                    default -> null;
                };
            }
            default -> null;
        };
    }

    private double getPlayerValue(PlayerStatistic playerStatistic, String stat) {
        return switch (stat) {
            case "health" -> playerStatistic.getHealth();
            case "defense" -> playerStatistic.getDefense();
            case "strength" -> playerStatistic.getStrength();
            case "intelligence" -> playerStatistic.getIntelligence();
            case "critChance" -> playerStatistic.getCritChance();
            case "critDamage" -> playerStatistic.getCritDamage();
            case "bonusAttackSpeed" -> playerStatistic.getBonusAttackSpeed();
            case "trueDefense" -> playerStatistic.getTrueDefense();
            case "ferocity" -> playerStatistic.getFerocity();
            case "healthRegen" -> playerStatistic.getHealthRegen();
            case "vitality" -> playerStatistic.getVitality();
            case "swingRange" -> playerStatistic.getSwingRange();

            case "speed" -> playerStatistic.getSpeed();

            case "absorption" -> playerStatistic.getAbsorption();
            case "damage" -> playerStatistic.getDamage();
            case "mana" -> playerStatistic.getMana();
            case "trueDamage" -> playerStatistic.getTrueDamage();
            default -> throw new IllegalStateException("Unexpected stat: " + stat);
        };
    }

    private double getPlayerMaxValue(PlayerStatistic playerStatistic, String stat) {
        return switch (stat) {
            case "health" -> playerStatistic.getMaxHealth();
            case "mana" -> playerStatistic.getManaPool();
            default -> getPlayerValue(playerStatistic, stat);
        };
    }

    private String getStatisticName(String stat) {
        return switch (stat) {
            case "health" -> Statistic.CombatStats.HEALTH.getName();
            case "defense" -> Statistic.CombatStats.DEFENSE.getName();
            case "strength" -> Statistic.CombatStats.STRENGTH.getName();
            case "intelligence" -> Statistic.CombatStats.INTELLIGENCE.getName();
            case "critChance" -> Statistic.CombatStats.CRIT_CHANCE.getName();
            case "critDamage" -> Statistic.CombatStats.CRIT_DAMAGE.getName();
            case "bonusAttackSpeed" -> Statistic.CombatStats.BONUS_ATTACK_SPEED.getName();
            case "trueDefense" -> Statistic.CombatStats.TRUE_DEFENSE.getName();
            case "ferocity" -> Statistic.CombatStats.FEROCITY.getName();
            case "healthRegen" -> Statistic.CombatStats.HEALTH_REGEN.getName();
            case "vitality" -> Statistic.CombatStats.VITALITY.getName();
            case "swingRange" -> Statistic.CombatStats.SWING_RANGE.getName();

            case "speed" -> Statistic.MiscStats.SPEED.getName();

            case "absorption" -> Statistic.OtherStats.ABSORPTION.getName();
            case "damage" -> Statistic.OtherStats.DAMAGE.getName();
            case "trueDamage" -> Statistic.OtherStats.TRUE_DAMAGE.getName();
            default -> throw new IllegalStateException("Unexpected stat: " + stat);
        };
    }

    private Color getStatisticColor(String stat) {
        return switch (stat) {
            case "health" -> Statistic.CombatStats.HEALTH.getColor();
            case "defense" -> Statistic.CombatStats.DEFENSE.getColor();
            case "strength" -> Statistic.CombatStats.STRENGTH.getColor();
            case "intelligence" -> Statistic.CombatStats.INTELLIGENCE.getColor();
            case "critChance" -> Statistic.CombatStats.CRIT_CHANCE.getColor();
            case "critDamage" -> Statistic.CombatStats.CRIT_DAMAGE.getColor();
            case "bonusAttackSpeed" -> Statistic.CombatStats.BONUS_ATTACK_SPEED.getColor();
            case "trueDefense" -> Statistic.CombatStats.TRUE_DEFENSE.getColor();
            case "ferocity" -> Statistic.CombatStats.FEROCITY.getColor();
            case "healthRegen" -> Statistic.CombatStats.HEALTH_REGEN.getColor();
            case "vitality" -> Statistic.CombatStats.VITALITY.getColor();
            case "swingRange" -> Statistic.CombatStats.SWING_RANGE.getColor();

            case "speed" -> Statistic.MiscStats.SPEED.getColor();

            case "absorption" -> Statistic.OtherStats.ABSORPTION.getColor();
            case "damage" -> Statistic.OtherStats.DAMAGE.getColor();
            case "trueDamage" -> Statistic.OtherStats.TRUE_DAMAGE.getColor();
            default -> throw new IllegalStateException("Unexpected stat: " + stat);
        };
    }

    private char getStatisticSymbol(String stat) {
        return switch (stat) {
            case "health" -> Statistic.CombatStats.HEALTH.getSymbol();
            case "defense" -> Statistic.CombatStats.DEFENSE.getSymbol();
            case "strength" -> Statistic.CombatStats.STRENGTH.getSymbol();
            case "intelligence" -> Statistic.CombatStats.INTELLIGENCE.getSymbol();
            case "critChance" -> Statistic.CombatStats.CRIT_CHANCE.getSymbol();
            case "critDamage" -> Statistic.CombatStats.CRIT_DAMAGE.getSymbol();
            case "bonusAttackSpeed" -> Statistic.CombatStats.BONUS_ATTACK_SPEED.getSymbol();
            case "trueDefense" -> Statistic.CombatStats.TRUE_DEFENSE.getSymbol();
            case "ferocity" -> Statistic.CombatStats.FEROCITY.getSymbol();
            case "healthRegen" -> Statistic.CombatStats.HEALTH_REGEN.getSymbol();
            case "vitality" -> Statistic.CombatStats.VITALITY.getSymbol();
            case "swingRange" -> Statistic.CombatStats.SWING_RANGE.getSymbol();

            case "speed" -> Statistic.MiscStats.SPEED.getSymbol();

            case "absorption" -> Statistic.OtherStats.ABSORPTION.getSymbol();
            case "damage" -> Statistic.OtherStats.DAMAGE.getSymbol();
            case "trueDamage" -> Statistic.OtherStats.TRUE_DAMAGE.getSymbol();
            default -> throw new IllegalStateException("Unexpected stat: " + stat);
        };
    }

    private double getStatisticBaseValue(String stat) {
        return switch (stat) {
            case "health" -> Statistic.CombatStats.HEALTH.getBaseValue();
            case "defense" -> Statistic.CombatStats.DEFENSE.getBaseValue();
            case "strength" -> Statistic.CombatStats.STRENGTH.getBaseValue();
            case "intelligence" -> Statistic.CombatStats.INTELLIGENCE.getBaseValue();
            case "critChance" -> Statistic.CombatStats.CRIT_CHANCE.getBaseValue();
            case "critDamage" -> Statistic.CombatStats.CRIT_DAMAGE.getBaseValue();
            case "bonusAttackSpeed" -> Statistic.CombatStats.BONUS_ATTACK_SPEED.getBaseValue();
            case "trueDefense" -> Statistic.CombatStats.TRUE_DEFENSE.getBaseValue();
            case "ferocity" -> Statistic.CombatStats.FEROCITY.getBaseValue();
            case "healthRegen" -> Statistic.CombatStats.HEALTH_REGEN.getBaseValue();
            case "vitality" -> Statistic.CombatStats.VITALITY.getBaseValue();
            case "swingRange" -> Statistic.CombatStats.SWING_RANGE.getBaseValue();

            case "speed" -> Statistic.MiscStats.SPEED.getBaseValue();

            case "absorption" -> Statistic.OtherStats.ABSORPTION.getBaseValue();
            case "damage" -> Statistic.OtherStats.DAMAGE.getBaseValue();
            case "trueDamage" -> Statistic.OtherStats.TRUE_DAMAGE.getBaseValue();
            default -> throw new IllegalStateException("Unexpected stat: " + stat);
        };
    }

    private double getStatisticMaxValue(String stat) {
        return switch (stat) {
            case "health" -> Statistic.CombatStats.HEALTH.getMaxValue();
            case "defense" -> Statistic.CombatStats.DEFENSE.getMaxValue();
            case "strength" -> Statistic.CombatStats.STRENGTH.getMaxValue();
            case "intelligence" -> Statistic.CombatStats.INTELLIGENCE.getMaxValue();
            case "critChance" -> Statistic.CombatStats.CRIT_CHANCE.getMaxValue();
            case "critDamage" -> Statistic.CombatStats.CRIT_DAMAGE.getMaxValue();
            case "bonusAttackSpeed" -> Statistic.CombatStats.BONUS_ATTACK_SPEED.getMaxValue();
            case "trueDefense" -> Statistic.CombatStats.TRUE_DEFENSE.getMaxValue();
            case "ferocity" -> Statistic.CombatStats.FEROCITY.getMaxValue();
            case "healthRegen" -> Statistic.CombatStats.HEALTH_REGEN.getMaxValue();
            case "vitality" -> Statistic.CombatStats.VITALITY.getMaxValue();
            case "swingRange" -> Statistic.CombatStats.SWING_RANGE.getMaxValue();

            case "speed" -> Statistic.MiscStats.SPEED.getMaxValue();

            case "absorption" -> Statistic.OtherStats.ABSORPTION.getMaxValue();
            case "damage" -> Statistic.OtherStats.DAMAGE.getMaxValue();
            case "trueDamage" -> Statistic.OtherStats.TRUE_DAMAGE.getMaxValue();
            default -> throw new IllegalStateException("Unexpected stat: " + stat);
        };
    }
}
