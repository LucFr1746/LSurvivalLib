package io.github.lucfr1746.LSurvivalLib.Entity;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerAPI;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatistic;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class StatisticCommands {

    private final LSurvivalLib plugin;

    public StatisticCommands(LSurvivalLib plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        new CommandAPICommand("lstats")
                .withPermission("lsurvival.lstats.view")
                .executesPlayer((player, args) -> {
                    PlayerAPI playerAPI = new PlayerAPI(player);
                    playerAPI.sendColoredMessage("&7[&aLStats&7] &2Running &a" + this.plugin.getDescription().getFullName() + "&2.");
                    playerAPI.sendColoredMessage("&7[&aLStats&7] &2Use &a/lstats help &2to view available commands.");
                })
                .withSubcommand(registerHelpSubCommand())
                .withSubcommand(registerViewSubCommand())
                .withSubcommand(registerSetSubCommand())
                .register();
    }

    private CommandAPICommand registerHelpSubCommand() {
        return new CommandAPICommand("help")
                .withPermission("lsurvival.lstats.view")
                .executesPlayer((player, args) -> {
                    sendHelpMessage(player);
                });
    }

    private CommandAPICommand registerViewSubCommand() {
        return new CommandAPICommand("view")
                .withPermission("lsurvival.lstats.view")
                .withOptionalArguments(new PlayerArgument("player"))
                .executesPlayer((player, args) -> {
                    Player target = (Player) args.getOrDefault("player", player);
                    player.sendMessage(new PlayerStatistic(target).toString());
                });
    }

    private CommandAPICommand registerSetSubCommand() {
        List<Argument<?>> arguments = List.of(
                new PlayerArgument("player"),
                new StringArgument("stat").replaceSuggestions(ArgumentSuggestions.strings(getAllStatNames())),
                new DoubleArgument("value", 0)
        );

        return new CommandAPICommand("set")
                .withPermission("lsurvival.lstats.modify")
                .withArguments(arguments)
                .executesPlayer(this::handleSetCommand);
    }

    private void handleSetCommand(Player player, CommandArguments args) {
        Player target = (Player) args.get("player");
        assert target != null;
        String statName = (String) args.get("stat");
        assert statName != null;
        double value = (double) args.get("value");

        PlayerStatistic targetStats = new PlayerStatistic(target);
        StatHandler statHandler = StatHandler.getHandler(statName);

        if (statHandler == null) {
            sendErrorMessage(player, "Invalid statistic: " + statName);
            return;
        }

        String error = statHandler.validateValue(value);
        if (error != null) {
            sendErrorMessage(player, error);
            return;
        }

        try {
            statHandler.applyValue(targetStats, value);
            sendSuccessMessage(player, target.getDisplayName(), statName, value);
        } catch (Exception e) {
            sendErrorMessage(player, "An error occurred while setting the statistic.");
            e.printStackTrace();
        }
    }

    private void sendGeneralHelp(Player player) {
        PlayerAPI playerAPI = new PlayerAPI(player);
        playerAPI.sendColoredMessage("&7[&aLStats&7] &2Running &a" + this.plugin.getDescription().getFullName() + "&2.");
        playerAPI.sendColoredMessage("&7[&aLStats&7] &2Use &a/lstats help &2to view available commands.");
    }

    private void sendHelpMessage(Player player) {
        PlayerAPI playerAPI = new PlayerAPI(player);
        playerAPI.sendColoredMessage("&7[&aLStats&7] &2Running &a" + this.plugin.getDescription().getFullName() + "&2.");
        playerAPI.sendColoredMessage("&2> /lstats help");
        playerAPI.sendColoredMessage("&2> /lstats view <player>");
        playerAPI.sendColoredMessage("&2> /lstats set <player> <stat> <value>");
    }

    private void sendErrorMessage(Player player, String message) {
        new PlayerAPI(player).sendColoredMessage("&7[&aLStats&7] &c" + message);
    }

    private void sendSuccessMessage(Player player, String targetName, String statName, double value) {
        new PlayerAPI(player).sendColoredMessage(
                String.format("&7[&aLStats&7] &aSuccessfully set %s &7\"&6%s&7\" &ato &6%.2f&a!", targetName, statName, value)
        );
    }

    private String[] getAllStatNames() {
        return Stream.of(
                        Statistic.CombatStats.values(),
                        Statistic.MiscStats.values(),
                        Statistic.OtherStats.values()
                ).flatMap(Arrays::stream)
                .map(Enum::name)
                .toArray(String[]::new);
    }
}

class StatHandler {
    private final double minValue;
    private final double maxValue;
    private final BiConsumer<PlayerStatistic, Double> applier;

    public StatHandler(double minValue, double maxValue, BiConsumer<PlayerStatistic, Double> applier) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.applier = applier;
    }

    public static StatHandler getHandler(String statName) {
        return switch (statName) {
            case "HEALTH" -> new StatHandler(1, Statistic.CombatStats.HEALTH.getMaxValue(), PlayerStatistic::setMaxHealth);
            case "DEFENSE" -> new StatHandler(0, Statistic.CombatStats.DEFENSE.getMaxValue(), PlayerStatistic::setDefense);
            case "STRENGTH" -> new StatHandler(0, Statistic.CombatStats.STRENGTH.getMaxValue(), PlayerStatistic::setStrength);
            case "INTELLIGENCE" -> new StatHandler(0, Statistic.CombatStats.INTELLIGENCE.getMaxValue(), PlayerStatistic::setIntelligence);
            case "CRIT_CHANCE" -> new StatHandler(0, Statistic.CombatStats.CRIT_CHANCE.getMaxValue(), PlayerStatistic::setCritChance);
            case "CRIT_DAMAGE" -> new StatHandler(0, Statistic.CombatStats.CRIT_DAMAGE.getMaxValue(), PlayerStatistic::setCritDamage);
            case "BONUS_ATTACK_SPEED" -> new StatHandler(0, Statistic.CombatStats.BONUS_ATTACK_SPEED.getMaxValue(), PlayerStatistic::setBonusAttackSpeed);
            case "TRUE_DEFENSE" -> new StatHandler(0, Statistic.CombatStats.TRUE_DEFENSE.getMaxValue(), PlayerStatistic::setTrueDefense);
            case "HEALTH_REGEN" -> new StatHandler(0, Statistic.CombatStats.HEALTH_REGEN.getMaxValue(), PlayerStatistic::setHealthRegen);
            case "VITALITY" -> new StatHandler(0, Statistic.CombatStats.VITALITY.getMaxValue(), PlayerStatistic::setVitality);
            case "SWING_RANGE" -> new StatHandler(0, Statistic.CombatStats.SWING_RANGE.getMaxValue(), PlayerStatistic::setSwingRange);

            case "SPEED" -> new StatHandler(0, Statistic.MiscStats.SPEED.getMaxValue(), PlayerStatistic::setSpeed);

            case "ABSORPTION" -> new StatHandler(0, Statistic.OtherStats.ABSORPTION.getMaxValue(), PlayerStatistic::setAbsorption);
            case "DAMAGE" -> new StatHandler(0, Statistic.OtherStats.DAMAGE.getMaxValue(), PlayerStatistic::setDamage);
            case "TRUE_DAMAGE" -> new StatHandler(0, Statistic.OtherStats.TRUE_DAMAGE.getMaxValue(), PlayerStatistic::setTrueDamage);
            default -> null;
        };
    }

    public String validateValue(double value) {
        if (value < minValue || (maxValue != -1 && value > maxValue)) {
            if (maxValue == -1) {
                return String.format("Please enter a value higher than %.2f.", minValue);
            } else {
                return String.format("Please enter a value between %.2f and %.2f.", minValue, maxValue);
            }
        }
        return null;
    }

    public void applyValue(PlayerStatistic stats, double value) {
        applier.accept(stats, value);
    }
}
