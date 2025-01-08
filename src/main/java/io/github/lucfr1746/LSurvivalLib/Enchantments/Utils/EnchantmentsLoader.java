package io.github.lucfr1746.LSurvivalLib.Enchantments.Utils;

import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Category.Category;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Tier.Tier;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.ConfigAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.LoggerAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class EnchantmentsLoader {

    private final FileConfiguration enchantmentsConfig;
    private static final Map<Enchantment, EnchantmentRegister> enchantments = new HashMap<>();

    public EnchantmentsLoader(LSurvivalLib plugin) {
        ConfigAPI configAPI = new ConfigAPI(plugin);
        File enchantmentsFolder = configAPI.createFolder(plugin.getDataFolder().getPath(), "enchantments");
        configAPI.createDefaultYamlFileConfiguration(enchantmentsFolder.getPath(), "enchantments/", "enchantments.yml");
        this.enchantmentsConfig = configAPI.getYamlConfiguration(enchantmentsFolder.getPath(), "enchantments.yml");

        loadEnchantments();
        new LoggerAPI(plugin).success("Registered " + enchantments.size() + "/" + Enchantment.values().length + " enchantments.");
    }

    private void loadEnchantments() {
        Set<String> enchantmentKeys = this.enchantmentsConfig.getKeys(false);
        for (String enchantmentKey : enchantmentKeys) {
            if (Arrays.stream(Enchantment.values()).noneMatch(enchantment -> enchantment.name().equals(enchantmentKey))) continue;

            String name = getEnchantmentName(enchantmentKey);
            int minLevel = getEnchantmentMinLevel(enchantmentKey);
            int maxLevel = getEnchantmentMaxLevel(enchantmentKey);
            int tableMinLevel = getEnchantmentTableMinLevel(enchantmentKey);
            int tableMaxLevel = getEnchantmentTableMaxLevel(enchantmentKey);
            Map<Integer, Integer> tableApplyCost = getEnchantmentEnchantingTableApplyCost(enchantmentKey);
            int anvilMaxCombine = getEnchantmentMaxAnvilCombine(enchantmentKey);
            int anvilMaxApply = getEnchantmentMaxAnvilApply(enchantmentKey);
            Map<Integer, Integer> anvilApplyCost = getEnchantmentAnvilApplyCost(enchantmentKey);
            Map<Integer, Tier> rarities = getEnchantmentRarities(enchantmentKey);
            Map<Integer, String> description = getEnchantmentDescription(enchantmentKey);
            List<String> sources = getEnchantmentSources(enchantmentKey);
            List<Category> supportedCategories = getEnchantmentSupportedCategories(enchantmentKey);
            List<Enchantment> exclusiveEnchantments = getExclusiveEnchantments(enchantmentKey);
            int enchantingLevelRequired = getEnchantingLevelRequired(enchantmentKey);
            int bookshelfPowerRequired = getEnchantmentBookshelfPowerRequired(enchantmentKey);

            enchantments.put(Enchantment.valueOf(enchantmentKey), new EnchantmentRegister(
                    name, minLevel, maxLevel, tableMinLevel, tableMaxLevel,
                    tableApplyCost, anvilMaxCombine, anvilMaxApply, anvilApplyCost,
                    rarities, description, sources, supportedCategories,
                    exclusiveEnchantments, enchantingLevelRequired, bookshelfPowerRequired
            ));
        }
    }

    public static Map<Enchantment, EnchantmentRegister> getAllRegisteredEnchantments() {
        return Collections.unmodifiableMap(enchantments);
    }

    public static EnchantmentRegister getEnchantmentRegister(Enchantment enchantment) {
        return enchantments.get(enchantment);
    }

    private String getEnchantmentName(String enchantmentKey) {
        return enchantmentsConfig.getString(enchantmentKey + ".name", "Unknown");
    }

    private int getEnchantmentMinLevel(String enchantmentKey) {
        return enchantmentsConfig.getInt(enchantmentKey + ".min-level", 0);
    }

    private int getEnchantmentMaxLevel(String enchantmentKey) {
        return enchantmentsConfig.getInt(enchantmentKey + ".max-level", 0);
    }

    private int getEnchantmentTableMinLevel(String enchantmentKey) {
        return enchantmentsConfig.getInt(enchantmentKey + ".enchanting-table.min-level", 0);
    }

    private int getEnchantmentTableMaxLevel(String enchantmentKey) {
        return enchantmentsConfig.getInt(enchantmentKey + ".enchanting-table.max-level", 0);
    }

    private Map<Integer, Integer> getEnchantmentEnchantingTableApplyCost(String enchantmentKey) {
        Map<Integer, Integer> costs = new HashMap<>();
        var section = enchantmentsConfig.getConfigurationSection(enchantmentKey + ".enchanting-table.cost");
        if (section != null) {
            for (String costKey : section.getKeys(false)) {
                costs.put(Integer.parseInt(costKey), section.getInt(costKey));
            }
        }
        return costs;
    }

    private int getEnchantmentMaxAnvilCombine(String enchantmentKey) {
        return enchantmentsConfig.getInt(enchantmentKey + ".anvil.max-anvil-combine", 0);
    }

    private int getEnchantmentMaxAnvilApply(String enchantmentKey) {
        return enchantmentsConfig.getInt(enchantmentKey + ".anvil.max-anvil-apply", 0);
    }

    private Map<Integer, Integer> getEnchantmentAnvilApplyCost(String enchantmentKey) {
        Map<Integer, Integer> costs = new HashMap<>();
        var section = enchantmentsConfig.getConfigurationSection(enchantmentKey + ".anvil.cost");
        if (section != null) {
            for (String costKey : section.getKeys(false)) {
                costs.put(Integer.parseInt(costKey), section.getInt(costKey));
            }
        }
        return costs;
    }

    private Map<Integer, Tier> getEnchantmentRarities(String enchantmentKey) {
        Map<Integer, Tier> rarities = new HashMap<>();
        var section = enchantmentsConfig.getConfigurationSection(enchantmentKey + ".rarities");
        if (section != null) {
            for (String rarityKey : section.getKeys(false)) {
                String rarity = section.getString(rarityKey);
                if (rarity != null && Arrays.stream(Tier.values()).anyMatch(tier -> tier.name().equals(rarity))) {
                    rarities.put(Integer.parseInt(rarityKey), Tier.valueOf(rarity));
                }
            }
        }
        return rarities;
    }

    private Map<Integer, String> getEnchantmentDescription(String enchantmentKey) {
        Map<Integer, String> descriptions = new HashMap<>();
        var section = enchantmentsConfig.getConfigurationSection(enchantmentKey + ".description");
        if (section != null) {
            for (String descriptionKey : section.getKeys(false)) {
                descriptions.put(Integer.parseInt(descriptionKey), section.getString(descriptionKey, "No description"));
            }
        }
        return descriptions;
    }

    private List<String> getEnchantmentSources(String enchantmentKey) {
        return enchantmentsConfig.getStringList(enchantmentKey + ".sources");
    }

    private List<Category> getEnchantmentSupportedCategories(String enchantmentKey) {
        List<Category> categories = new ArrayList<>();
        for (String category : enchantmentsConfig.getStringList(enchantmentKey + ".support-categories")) {
            if (category.equals("ARMOR")) {
                categories.add(Category.HELMET);
                categories.add(Category.CHESTPLATE);
                categories.add(Category.LEGGINGS);
                categories.add(Category.BOOTS);
            } else if (category.equals("TOOLS")) {
                categories.add(Category.PICKAXE);
                categories.add(Category.SHEARS);
                categories.add(Category.SHOVEL);
                categories.add(Category.HOE);
                categories.add(Category.AXE);
            } else {
                if (Arrays.stream(Category.values()).anyMatch(c -> c.name().equals(category))) {
                    categories.add(Category.valueOf(category));
                }
            }
        }
        return categories;
    }

    private List<Enchantment> getExclusiveEnchantments(String enchantmentKey) {
        List<Enchantment> conflicts = new ArrayList<>();
        for (String conflictKey : enchantmentsConfig.getStringList(enchantmentKey + ".conflicts")) {
            if (Arrays.stream(Enchantment.values()).anyMatch(e -> e.name().equals(conflictKey))) {
                conflicts.add(Enchantment.valueOf(conflictKey));
            }
        }
        return conflicts;
    }

    private int getEnchantingLevelRequired(String enchantmentKey) {
        var section = enchantmentsConfig.getConfigurationSection(enchantmentKey + ".requirements");
        if (section != null) {
            for (String requirementKey : section.getKeys(false)) {
                String type = section.getString(requirementKey + ".type");
                if ("SKILL".equals(type) && Objects.equals(section.getString(requirementKey + ".skill"), "ENCHANTING")) {
                    return section.getInt(requirementKey + ".level", 0);
                }
            }
        }
        return 0;
    }

    private int getEnchantmentBookshelfPowerRequired(String enchantmentKey) {
        var section = enchantmentsConfig.getConfigurationSection(enchantmentKey + ".requirements");
        if (section != null) {
            for (String requirementKey : section.getKeys(false)) {
                String type = section.getString(requirementKey + ".type");
                if ("BOOKSHELF_POWER".equalsIgnoreCase(type)) {
                    return section.getInt(requirementKey + ".amount", 0);
                }
            }
        }
        return 0;
    }
}