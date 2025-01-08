package io.github.lucfr1746.LSurvivalLib.Entity;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatisticExpansion;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.ColorAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.ConfigAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.LoggerAPI;
import org.bukkit.Color;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.function.BiConsumer;

public class Statistic {

    private final LSurvivalLib plugin;
    private final FileConfiguration statisticConfig;

    private static boolean actionBar_playerStatsOverlay_enabled = true;
    private static int    actionBar_playerStatsOverlay_priority = 1;
    private static String  actionBar_playerStatsOverlay_message = "%player_stat_health_color%%player_stat_health%/%player_stat_max_health%%player_stat_health_symbol%     %player_stat_defense_color%%player_stat_defense%%player_stat_defense_symbol% %player_stat_defense_name%     %player_stat_mana_color%%player_stat_mana%/%player_stat_mana_pool%%player_stat_mana_symbol%";

    public Statistic(LSurvivalLib plugin) {
        this.plugin = plugin;
        File statisticFolder = new ConfigAPI(plugin).createFolder(plugin.getDataFolder().getPath(), "statistic");
        new ConfigAPI(plugin).createDefaultYamlFileConfiguration(statisticFolder.getPath(), "statistic/", "config.yml");
        this.statisticConfig = new ConfigAPI(plugin).getYamlConfiguration(statisticFolder.getPath(), "config.yml");

        new StatisticCommands(plugin);
        registerPlaceholder();
        registerActionBar();
        registerStats();
    }

    private void registerPlaceholder() {
        try {
            new PlayerStatisticExpansion(this.plugin).register();
            new LoggerAPI(this.plugin).success("Successfully hook into PlaceholderAPI!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getActionBar_playerStatsOverlay_enabled() {
        return actionBar_playerStatsOverlay_enabled;
    }

    public static int getActionBar_playerStatsOverlay_priority() {
        return actionBar_playerStatsOverlay_priority;
    }

    public static String getActionBar_playerStatsOverlay_message() {
        return actionBar_playerStatsOverlay_message;
    }

    private void registerActionBar() {
        actionBar_playerStatsOverlay_enabled = this.statisticConfig.getBoolean("action-bar.player-stats-overlay.enabled", actionBar_playerStatsOverlay_enabled);
        actionBar_playerStatsOverlay_priority = this.statisticConfig.getInt("action-bar.player-stats-overlay.priority", actionBar_playerStatsOverlay_priority);
        actionBar_playerStatsOverlay_message = this.statisticConfig.getString("action-bar.player-stats-overlay.message", actionBar_playerStatsOverlay_message);
    }

    private void registerStats() {
        CombatStats.loadFromConfig(this.statisticConfig);
        GatheringStats.loadFromConfig(this.statisticConfig);
        WisdomStats.loadFromConfig(this.statisticConfig);
        MiscStats.loadFromConfig(this.statisticConfig);
        OtherStats.loadFromConfig(this.statisticConfig);
    }

    public enum CombatStats {
        HEALTH("Health",'❤', Color.RED,"health",100,-1),
        DEFENSE("Defense",'❈', Color.LIME,"defense",0,-1),
        STRENGTH("Strength",'❁', Color.RED,"strength",0,-1),
        INTELLIGENCE("Intelligence",'✎', Color.AQUA,"intelligence",0,-1),
        CRIT_CHANCE("Crit Change",'☣', Color.BLUE,"crit-chance",30,-1),
        CRIT_DAMAGE("Crit Damage",'☠', Color.BLUE,"crit-damage",50,-1),
        BONUS_ATTACK_SPEED("Bonus Attack Speed",'⚔', Color.YELLOW,"bonus-attack-speed",0,100),
        TRUE_DEFENSE("True Defense",'❂', Color.WHITE,"true-defense",0,-1),
        FEROCITY("Ferocity",'⫽', Color.RED,"ferocity",0,500),
        HEALTH_REGEN("Health Regen",'❣', Color.RED,"health-regen",100,-1),
        VITALITY("Vitality",'♨', Color.MAROON,"vitality",0,-1),
        SWING_RANGE("Swing Range",'Ⓢ', Color.YELLOW,"swing-range",0,15);

        private String name;
        private final char symbol;
        private Color color;
        private final String configKey;
        private double baseValue;
        private double maxValue;

        CombatStats(String name, char symbol, Color color, String configKey, double baseValue, double maxValue) {
            this.name = name;
            this.symbol = symbol;
            this.color = color;
            this.configKey = configKey;
            this.baseValue = baseValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getConfigKey() {
            return configKey;
        }

        public double getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public static void loadFromConfig(FileConfiguration config) {
            LoggerAPI logger = new LoggerAPI("LSurvivalLib");

            for (CombatStats stat : CombatStats.values()) {
                String basePath = "statistic.combat-stats." + stat.getConfigKey() + ".";

                // Load and set value with logging
                loadStatValue(config, logger, stat, basePath, "name", stat.getName(), (s, v) -> s.setName((String) v));
                loadStatValue(config, logger, stat, basePath, "color", ColorAPI.colorToHex(stat.getColor()),
                        (s, v) -> s.setColor(ColorAPI.hexToColor((String) v)));
                loadStatValue(config, logger, stat, basePath, "base-value", stat.getBaseValue(), (s, v) -> s.setBaseValue((double) v));
                loadStatValue(config, logger, stat, basePath, "max-value", stat.getMaxValue(),
                        (s, v) -> s.setMaxValue(Math.max((double) v, -1)));
            }
        }

        private static void loadStatValue(FileConfiguration config, LoggerAPI logger, CombatStats stat, String basePath,
                                          String key, Object defaultValue, BiConsumer<CombatStats, Object> setter) {
            String path = basePath + key;

            if (config.isString(path) || config.isDouble(path) || config.isInt(path)) {
                Object value = getValueFromConfig(config, path, defaultValue);
                setter.accept(stat, value);
            } else {
                logger.warning("Missing " + key + " for " + stat.getConfigKey() + ", using default: " + defaultValue);
            }
        }

        private static Object getValueFromConfig(FileConfiguration config, String path, Object defaultValue) {
            if (defaultValue instanceof String) {
                return config.getString(path, (String) defaultValue);
            } else if (defaultValue instanceof Double) {
                return config.getDouble(path, (Double) defaultValue);  // No need for casting (double) defaultValue
            } else if (defaultValue instanceof Integer) {
                return config.getInt(path, (Integer) defaultValue);  // Use getInt for integer values
            }
            return defaultValue;
        }
    }

    public enum GatheringStats {
        MINING_SPEED("Mining Speed",'⸕', Color.ORANGE,"mining-speed",0,-1),
        MINING_FORTUNE("Mining Fortune",'☘', Color.ORANGE,"mining-fortune",0,-1),
        MINING_SPREAD("Mining Spread",'▚', Color.ORANGE,"mining-spread",0,-1),
        FARMING_FORTUNE("Farming Fortune",'☘', Color.ORANGE,"farming-fortune",0,-1),
        FORAGING_FORTUNE("Foraging Fortune",'☘', Color.ORANGE,"foraging-fortune",0,-1);

        private String name;
        private final char symbol;
        private Color color;
        private final String configKey;
        private double baseValue;
        private double maxValue;

        GatheringStats(String name, char symbol, Color color, String configKey, double baseValue, double maxValue) {
            this.name = name;
            this.symbol = symbol;
            this.color = color;
            this.configKey = configKey;
            this.baseValue = baseValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getConfigKey() {
            return configKey;
        }

        public double getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public static void loadFromConfig(FileConfiguration config) {
            LoggerAPI logger = new LoggerAPI("LSurvivalLib");

            for (GatheringStats stat : GatheringStats.values()) {
                String basePath = "statistic.gathering-stats." + stat.getConfigKey() + ".";

                // Load and set value with logging
                loadStatValue(config, logger, stat, basePath, "name", stat.getName(), (s, v) -> s.setName((String) v));
                loadStatValue(config, logger, stat, basePath, "color", ColorAPI.colorToHex(stat.getColor()),
                        (s, v) -> s.setColor(ColorAPI.hexToColor((String) v)));
                loadStatValue(config, logger, stat, basePath, "base-value", stat.getBaseValue(), (s, v) -> s.setBaseValue((double) v));
                loadStatValue(config, logger, stat, basePath, "max-value", stat.getMaxValue(),
                        (s, v) -> s.setMaxValue(Math.max((double) v, -1)));
            }
        }

        private static void loadStatValue(FileConfiguration config, LoggerAPI logger, GatheringStats stat, String basePath,
                                          String key, Object defaultValue, BiConsumer<GatheringStats, Object> setter) {
            String path = basePath + key;

            if (config.isString(path) || config.isDouble(path) || config.isInt(path)) {
                Object value = getValueFromConfig(config, path, defaultValue);
                setter.accept(stat, value);
            } else {
                logger.warning("Missing " + key + " for " + stat.getConfigKey() + ", using default: " + defaultValue);
            }
        }

        private static Object getValueFromConfig(FileConfiguration config, String path, Object defaultValue) {
            if (defaultValue instanceof String) {
                return config.getString(path, (String) defaultValue);
            } else if (defaultValue instanceof Double) {
                return config.getDouble(path, (Double) defaultValue);  // No need for casting (double) defaultValue
            } else if (defaultValue instanceof Integer) {
                return config.getInt(path, (Integer) defaultValue);  // Use getInt for integer values
            }
            return defaultValue;
        }
    }

    public enum WisdomStats {
        ALCHEMY_WISDOM("Alchemy Wisdom",'☯', Color.TEAL,"alchemy",0,-1),
        CARPENTRY_WISDOM("Carpentry Wisdom",'☯', Color.TEAL,"carpentry",0,-1),
        COMBAT_WISDOM("Combat Wisdom",'☯', Color.TEAL,"combat",0,-1),
        ENCHANTING_WISDOM("Enchanting Wisdom",'☯', Color.TEAL,"enchanting",0,-1),
        FARMING_WISDOM("Farming Wisdom",'☯', Color.TEAL,"farming",0,-1),
        FISHING_WISDOM("Fishing Wisdom",'☯', Color.TEAL,"fishing",0,-1),
        FORAGING_WISDOM("Foraging Wisdom",'☯', Color.TEAL,"foraging",0,-1),
        MINING_WISDOM("Minning Wisdom",'☯', Color.TEAL,"mining",0,-1);

        private String name;
        private final char symbol;
        private Color color;
        private final String configKey;
        private double baseValue;
        private double maxValue;

        WisdomStats(String name, char symbol, Color color, String configKey, double baseValue, double maxValue) {
            this.name = name;
            this.symbol = symbol;
            this.color = color;
            this.configKey = configKey;
            this.baseValue = baseValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getConfigKey() {
            return configKey;
        }

        public double getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public static void loadFromConfig(FileConfiguration config) {
            LoggerAPI logger = new LoggerAPI("LSurvivalLib");

            for (WisdomStats stat : WisdomStats.values()) {
                String basePath = "statistic.wisdom-stats." + stat.getConfigKey() + ".";

                // Load and set value with logging
                loadStatValue(config, logger, stat, basePath, "name", stat.getName(), (s, v) -> s.setName((String) v));
                loadStatValue(config, logger, stat, basePath, "color", ColorAPI.colorToHex(stat.getColor()),
                        (s, v) -> s.setColor(ColorAPI.hexToColor((String) v)));
                loadStatValue(config, logger, stat, basePath, "base-value", stat.getBaseValue(), (s, v) -> s.setBaseValue((double) v));
                loadStatValue(config, logger, stat, basePath, "max-value", stat.getMaxValue(),
                        (s, v) -> s.setMaxValue(Math.max((double) v, -1)));
            }
        }

        private static void loadStatValue(FileConfiguration config, LoggerAPI logger, WisdomStats stat, String basePath,
                                          String key, Object defaultValue, BiConsumer<WisdomStats, Object> setter) {
            String path = basePath + key;

            if (config.isString(path) || config.isDouble(path) || config.isInt(path)) {
                Object value = getValueFromConfig(config, path, defaultValue);
                setter.accept(stat, value);
            } else {
                logger.warning("Missing " + key + " for " + stat.getConfigKey() + ", using default: " + defaultValue);
            }
        }

        private static Object getValueFromConfig(FileConfiguration config, String path, Object defaultValue) {
            if (defaultValue instanceof String) {
                return config.getString(path, (String) defaultValue);
            } else if (defaultValue instanceof Double) {
                return config.getDouble(path, (Double) defaultValue);  // No need for casting (double) defaultValue
            } else if (defaultValue instanceof Integer) {
                return config.getInt(path, (Integer) defaultValue);  // Use getInt for integer values
            }
            return defaultValue;
        }
    }

    public enum MiscStats {
        SPEED("Speed",'✦', Color.WHITE,"speed",100,500),
        MAGIC_FIND("Magic Find",'✯', Color.AQUA,"magic-find",0,-1);

        private String name;
        private final char symbol;
        private Color color;
        private final String configKey;
        private double baseValue;
        private double maxValue;

        MiscStats(String name, char symbol, Color color, String configKey, double baseValue, double maxValue) {
            this.name = name;
            this.symbol = symbol;
            this.color = color;
            this.configKey = configKey;
            this.baseValue = baseValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getConfigKey() {
            return configKey;
        }

        public double getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public static void loadFromConfig(FileConfiguration config) {
            LoggerAPI logger = new LoggerAPI("LSurvivalLib");

            for (MiscStats stat : MiscStats.values()) {
                String basePath = "statistic.misc-stats." + stat.getConfigKey() + ".";

                // Load and set value with logging
                loadStatValue(config, logger, stat, basePath, "name", stat.getName(), (s, v) -> s.setName((String) v));
                loadStatValue(config, logger, stat, basePath, "color", ColorAPI.colorToHex(stat.getColor()),
                        (s, v) -> s.setColor(ColorAPI.hexToColor((String) v)));
                loadStatValue(config, logger, stat, basePath, "base-value", stat.getBaseValue(), (s, v) -> s.setBaseValue((double) v));
                loadStatValue(config, logger, stat, basePath, "max-value", stat.getMaxValue(),
                        (s, v) -> s.setMaxValue(Math.max((double) v, -1)));
            }
        }

        private static void loadStatValue(FileConfiguration config, LoggerAPI logger, MiscStats stat, String basePath,
                                          String key, Object defaultValue, BiConsumer<MiscStats, Object> setter) {
            String path = basePath + key;

            if (config.isString(path) || config.isDouble(path) || config.isInt(path)) {
                Object value = getValueFromConfig(config, path, defaultValue);
                setter.accept(stat, value);
            } else {
                logger.warning("Missing " + key + " for " + stat.getConfigKey() + ", using default: " + defaultValue);
            }
        }

        private static Object getValueFromConfig(FileConfiguration config, String path, Object defaultValue) {
            if (defaultValue instanceof String) {
                return config.getString(path, (String) defaultValue);
            } else if (defaultValue instanceof Double) {
                return config.getDouble(path, (Double) defaultValue);  // No need for casting (double) defaultValue
            } else if (defaultValue instanceof Integer) {
                return config.getInt(path, (Integer) defaultValue);  // Use getInt for integer values
            }
            return defaultValue;
        }
    }

    public enum OtherStats {
        ABSORPTION("Absorption",'❤', Color.ORANGE,"absorption",0,-1),
        DAMAGE("Damage",'❁', Color.RED,"damage",5,-1),
        TRUE_DAMAGE("True Damage",'❂', Color.WHITE,"true-damage",0,-1);

        private String name;
        private final char symbol;
        private Color color;
        private final String configKey;
        private double baseValue;
        private double maxValue;

        OtherStats(String name, char symbol, Color color, String configKey, double baseValue, double maxValue) {
            this.name = name;
            this.symbol = symbol;
            this.color = color;
            this.configKey = configKey;
            this.baseValue = baseValue;
            this.maxValue = maxValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public char getSymbol() {
            return symbol;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public String getConfigKey() {
            return configKey;
        }

        public double getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(double baseValue) {
            this.baseValue = baseValue;
        }

        public double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(double maxValue) {
            this.maxValue = maxValue;
        }

        public static void loadFromConfig(FileConfiguration config) {
            LoggerAPI logger = new LoggerAPI("LSurvivalLib");

            for (OtherStats stat : OtherStats.values()) {
                String basePath = "statistic.other-stats." + stat.getConfigKey() + ".";

                // Load and set value with logging
                loadStatValue(config, logger, stat, basePath, "name", stat.getName(), (s, v) -> s.setName((String) v));
                loadStatValue(config, logger, stat, basePath, "color", ColorAPI.colorToHex(stat.getColor()),
                        (s, v) -> s.setColor(ColorAPI.hexToColor((String) v)));
                loadStatValue(config, logger, stat, basePath, "base-value", stat.getBaseValue(), (s, v) -> s.setBaseValue((double) v));
                loadStatValue(config, logger, stat, basePath, "max-value", stat.getMaxValue(),
                        (s, v) -> s.setMaxValue(Math.max((double) v, -1)));
            }
        }

        private static void loadStatValue(FileConfiguration config, LoggerAPI logger, OtherStats stat, String basePath,
                                          String key, Object defaultValue, BiConsumer<OtherStats, Object> setter) {
            String path = basePath + key;

            if (config.isString(path) || config.isDouble(path) || config.isInt(path)) {
                Object value = getValueFromConfig(config, path, defaultValue);
                setter.accept(stat, value);
            } else {
                logger.warning("Missing " + key + " for " + stat.getConfigKey() + ", using default: " + defaultValue);
            }
        }

        private static Object getValueFromConfig(FileConfiguration config, String path, Object defaultValue) {
            if (defaultValue instanceof String) {
                return config.getString(path, (String) defaultValue);
            } else if (defaultValue instanceof Double) {
                return config.getDouble(path, (Double) defaultValue);  // No need for casting (double) defaultValue
            } else if (defaultValue instanceof Integer) {
                return config.getInt(path, (Integer) defaultValue);  // Use getInt for integer values
            }
            return defaultValue;
        }
    }
}
