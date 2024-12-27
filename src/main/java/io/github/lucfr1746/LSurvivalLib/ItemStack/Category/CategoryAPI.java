package io.github.lucfr1746.LSurvivalLib.ItemStack.Category;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CategoryAPI {

    private final ItemStack itemStack;

    public CategoryAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public CategoryAPI setCategory(@NotNull Category category) {
        if (this.itemStack.getType() == Material.AIR) return this;
        NBT.modify(this.itemStack, nbt -> {
            nbt.getOrCreateCompound("ExtraAttributes").setString("category", category.name());
        });
        return this;
    }

    public Category getCategory() {
        if (this.itemStack.getType() == Material.AIR) return Category.UNCLASSIFIED;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (!nbtList.hasTag("category")) {
                nbtList.setString("category", getMaterialCategory().name());
            }
            try {
                return Category.valueOf(nbtList.getString("category"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
    }

    public Category getMaterialCategory() {
        String[] types = this.itemStack.getType().toString().split("_");
        String typeSuffix = types[types.length - 1];
        return Arrays.stream(Category.values())
                .filter(category -> category.name().equals(typeSuffix))
                .findFirst()
                .orElse(Category.NONE);
    }

    public List<Category> getNearCategoriesCircle() {
        Category currentCategory = getCategory();

        if (currentCategory == Category.UNCLASSIFIED) {
            return Arrays.asList(Category.UNCLASSIFIED, Category.UNCLASSIFIED, Category.UNCLASSIFIED);
        }

        List<Category> categoryList = Arrays.stream(Category.values())
                .filter(category -> category != Category.UNCLASSIFIED)
                .toList();

        boolean isArmor = currentCategory == Category.HELMET || currentCategory == Category.CHESTPLATE
                || currentCategory == Category.LEGGINGS || currentCategory == Category.BOOTS;
        String itemType = this.itemStack.getType().name();

        Category previousCategory = isArmor ? Category.NONE
                : (currentCategory == Category.NECKLACE ? switch (getArmorType(itemType)) {
            case "BOOTS" -> Category.BOOTS;
            case "CHESTPLATE" -> Category.CHESTPLATE;
            case "LEGGINGS" -> Category.LEGGINGS;
            case "HELMET" -> Category.HELMET;
            default -> Category.NONE;
        } : categoryList.get(categoryList.indexOf(currentCategory) - 1));

        Category nextCategory = isArmor ? Category.NECKLACE
                : (currentCategory == Category.NONE ? switch (getArmorType(itemType)) {
            case "HELMET" -> Category.HELMET;
            case "CHESTPLATE" -> Category.CHESTPLATE;
            case "LEGGINGS" -> Category.LEGGINGS;
            case "BOOTS" -> Category.BOOTS;
            default -> Category.NECKLACE;
        } : categoryList.get(categoryList.indexOf(currentCategory) + 1));

        return Arrays.asList(previousCategory, currentCategory, nextCategory);
    }

    private String getArmorType(String itemType) {
        if (itemType.endsWith("BOOTS")) return "BOOTS";
        if (itemType.endsWith("CHESTPLATE")) return "CHESTPLATE";
        if (itemType.endsWith("LEGGINGS")) return "LEGGINGS";
        if (itemType.endsWith("HELMET") || this.itemStack.getType().getMaxDurability() == 0) return "HELMET";
        return "";
    }
}
