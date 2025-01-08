package io.github.lucfr1746.LSurvivalLib.Enchantments.Utils;

import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Category.Category;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Tier.Tier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentRegister {

    private final String name;
    private final int minLevel, maxLevel;

    private final int enchantingTable_minLevel, enchantingTable_maxLevel;
    private final Map<Integer, Integer> enchantingTable_cost = new HashMap<>();

    private final int anvil_maxAnvilCombine, anvil_maxAnvilApply;
    private final Map<Integer, Integer> anvil_cost = new HashMap<>();

    private final Map<Integer, Tier> rarity = new HashMap<>();

    private final Map<Integer, String> description = new HashMap<>();

    private final List<String> sources = new ArrayList<>();

    private final List<Category> supportCategories = new ArrayList<>();

    private final List<Enchantment> exclusiveEnchantments = new ArrayList<>();

    // skill requirements ??
    // private final Map<Skill, Integer> skillRequirements = new HashMap<>();
    private final int enchantingSkillLevelRequired;

    private final int bookshelfPowerRequired;

    public EnchantmentRegister(String name,
                               int minLevel, int maxLevel,
                               int enchantingTable_minLevel, int enchantingTable_maxLevel, Map<Integer, Integer> enchantingTable_cost,
                               int anvil_maxAnvilCombine, int anvil_maxAnvilApply, Map<Integer, Integer> anvil_cost,
                               Map<Integer, Tier> rarity,
                               Map<Integer, String> description,
                               List<String> sources,
                               List<Category> supportCategories,
                               List<Enchantment> exclusiveEnchantments,
                               int enchantingSkillLevelRequired,
                               int bookshelfPowerRequired) {
        this.name = name;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;

        this.enchantingTable_minLevel = enchantingTable_minLevel;
        this.enchantingTable_maxLevel = enchantingTable_maxLevel;
        this.enchantingTable_cost.putAll(enchantingTable_cost);

        this.anvil_maxAnvilCombine = anvil_maxAnvilCombine;
        this.anvil_maxAnvilApply = anvil_maxAnvilApply;
        this.anvil_cost.putAll(anvil_cost);

        this.rarity.putAll(rarity);
        this.description.putAll(description);
        this.sources.addAll(sources);
        this.supportCategories.addAll(supportCategories);
        this.exclusiveEnchantments.addAll(exclusiveEnchantments);
        this.enchantingSkillLevelRequired = enchantingSkillLevelRequired;
        this.bookshelfPowerRequired = bookshelfPowerRequired;
    }

    public String getName() {
        return name;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getEnchantingTableMinLevel() {
        return enchantingTable_minLevel;
    }

    public int getEnchantingTableMaxLevel() {
        return enchantingTable_maxLevel;
    }

    public int getEnchantingTableApplyCostForLevel(int level) {
        return enchantingTable_cost.getOrDefault(level, 0);
    }

    public Map<Integer, Integer> getEnchantingTableCost() {
        return enchantingTable_cost;
    }

    public int getAnvilMaxAnvilCombine() {
        return anvil_maxAnvilCombine;
    }

    public int getAnvilMaxAnvilApply() {
        return anvil_maxAnvilApply;
    }

    public int getAnvilApplyCostForLevel(int level) {
        return anvil_cost.getOrDefault(level, 0);
    }

    public Map<Integer, Integer> getAnvilCost() {
        return anvil_cost;
    }

    public Tier getRarityForLevel(int level) {
        return rarity.get(level);
    }

    public String getDescriptionForLevel(int level) {
        return description.get(level);
    }

    public List<String> getSources() {
        return sources;
    }

    public List<Category> getSupportCategories() {
        return supportCategories;
    }

    public List<Enchantment> getExclusiveEnchantments() {
        return exclusiveEnchantments;
    }

    public boolean isSupportedCategory(Category category) {
        return supportCategories.contains(category);
    }

    public int getEnchantingSkillLevelRequired() {
        return enchantingSkillLevelRequired;
    }

    public int getBookshelfPowerRequired() {
        return bookshelfPowerRequired;
    }
}
