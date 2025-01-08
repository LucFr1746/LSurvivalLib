package io.github.lucfr1746.LSurvivalLib.ItemStack;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Utils.EnchantmentRegister;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Utils.EnchantmentsLoader;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Category.Category;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Category.CategoryAPI;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Tier.Tier;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Tier.TierAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.ColorAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.NumberAPI;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.TextAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemBuilderAPI {


    @NotNull private final ItemStack itemStack;
    private String displayName;

    private Tier tier;
    private List<Tier> nearestTiers;

    private Category category;
    private List<Category> nearestCategories;

    public ItemBuilderAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        initializeItem();
    }

    public ItemBuilderAPI(@NotNull Material material) {
        this(new ItemStack(material));
    }

    private void initializeItem() {
        loadBasicProperties();
        loadTierData();
        loadCategoryData();
    }

    private void loadBasicProperties() {
        this.displayName = getDisplayName();
    }

    private void loadTierData() {
        TierAPI tierAPI = new TierAPI(itemStack);
        this.tier = tierAPI.getTier();
        this.nearestTiers = tierAPI.getNearTiersCircle();
    }

    private void loadCategoryData() {
        CategoryAPI categoryAPI = new CategoryAPI(itemStack);
        this.category = categoryAPI.getCategory();
        this.nearestCategories = categoryAPI.getNearCategoriesCircle();
    }

    public @NotNull ItemStack build() {
        if (this.category == Category.UNCLASSIFIED) return this.itemStack;

        setItemName(this.displayName);

        List<String> finalLores = new ArrayList<>();
    // Leather color, flight duration, breaking power
        if (getType().name().startsWith("LEATHER_")) finalLores.add("&7Color: " + getItemHex());
        if (getItemMeta() instanceof FireworkMeta && getFireWorkPower() > 0) finalLores.add("&7Flight Duration: " + getFireWorkPower());
        if (getBreakingPower() > 0) finalLores.add("&8Breaking Power " + NumberAPI.toStringFixed(getBreakingPower(),0));
    // Add stats lore
        if (!finalLores.isEmpty()) finalLores.add("");
        finalLores.addAll(buildStatLore());
    // Enchantments lore
        if (!finalLores.isEmpty() && !getEnchantments().isEmpty()) {
            finalLores.add("");
        }
        finalLores.addAll(buildEnchantmentsLore());

    // Item's description
        if (!getDescription().isEmpty()) {
            if (!finalLores.isEmpty()) finalLores.add("");
            finalLores.addAll(getDescriptionAutoAlignLores(getDescriptionLineLength()));
        }

    // Add category description
        if (!finalLores.isEmpty()) finalLores.add("");
        if (category.canBeReforged()) {
            finalLores.add("&8This item can be reforged!");
        }

    // Add rarity lore
        finalLores.add(buildRarityLore());
        return setLores(finalLores).setAllFlags().itemStack;
    }

    private List<String> buildStatLore() {
        List<String> statsLore = new ArrayList<>();

        addStatLore(statsLore, Statistic.OtherStats.DAMAGE.getName(), getDamage(), "&c");
        addStatLore(statsLore, Statistic.OtherStats.TRUE_DAMAGE.getName(), getTrueDamage(), "&c");
        addStatLore(statsLore, Statistic.CombatStats.STRENGTH.getName(), getStrength(), "&c");
        addStatLore(statsLore, Statistic.CombatStats.CRIT_CHANCE.getName(), getCritChance(), "&c", "%");
        addStatLore(statsLore, Statistic.CombatStats.CRIT_CHANCE.getName(), getCritDamage(), "&c", "%");
        addStatLore(statsLore, Statistic.CombatStats.BONUS_ATTACK_SPEED.getName(), getBonusAttackSpeed(), "&c", "%");

        addStatLore(statsLore, Statistic.MiscStats.MAGIC_FIND.getName(), getMagicFind(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.HEALTH.getName(), getHealth(), "&a");
        addStatLore(statsLore, Statistic.OtherStats.ABSORPTION.getName(), getAbsorption(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.DEFENSE.getName(), getDefense(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.TRUE_DEFENSE.getName(), getTrueDefense(), "&a");
        addStatLore(statsLore, Statistic.GatheringStats.MINING_SPEED.getName(), getMiningSpeed(), "&a");
        addStatLore(statsLore, Statistic.GatheringStats.MINING_FORTUNE.getName(), getMiningFortune(), "&a");
        addStatLore(statsLore, Statistic.GatheringStats.FARMING_FORTUNE.getName(), getFarmingFortune(), "&a");
        addStatLore(statsLore, Statistic.GatheringStats.FORAGING_FORTUNE.getName(), getForagingFortune(), "&a");
        addStatLore(statsLore, Statistic.GatheringStats.MINING_SPREAD.getName(), getMiningSpread(), "&a");
        addStatLore(statsLore, Statistic.MiscStats.SPEED.getName(), getWalkSpeed(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.INTELLIGENCE.getName(), getIntelligence(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.HEALTH_REGEN.getName(), getHealthRegen(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.FEROCITY.getName(), getFerocity(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.VITALITY.getName(), getVitality(), "&a");
        addStatLore(statsLore, Statistic.CombatStats.SWING_RANGE.getName(), getSwingRange(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.ALCHEMY_WISDOM.getName(), getAlchemyWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.CARPENTRY_WISDOM.getName(), getCarpentryWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.COMBAT_WISDOM.getName(), getCombatWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.ENCHANTING_WISDOM.getName(), getEnchantingWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.FARMING_WISDOM.getName(), getFarmingWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.FISHING_WISDOM.getName(), getFishingWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.FORAGING_WISDOM.getName(), getForagingWisdom(), "&a");
        addStatLore(statsLore, Statistic.WisdomStats.MINING_WISDOM.getName(), getMiningWisdom(), "&a");

        return statsLore;
    }

    private List<String> buildEnchantmentsLore() {
        List<String> enchantmentsLore = new ArrayList<>();
        Map<Enchantment, Integer> enchantments = getEnchantments();

        int applyCost = 0;

        if (enchantments == null || enchantments.isEmpty()) {
            return enchantmentsLore;
        }

        if (enchantments.size() >= 5) {
            StringBuilder lore = new StringBuilder().append("&9");
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                EnchantmentRegister enchantmentRegister = EnchantmentsLoader.getEnchantmentRegister(enchantment);
                lore.append(", ").append(enchantmentRegister.getName()).append(" ").append(NumberAPI.toRoman(entry.getValue()));
            }
            enchantmentsLore.addAll(getAutoAlignLores(33, lore.toString().replaceFirst(", ", "")));
        } else {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                EnchantmentRegister enchantmentRegister = EnchantmentsLoader.getEnchantmentRegister(enchantment);
                int level = entry.getValue();

                String name = enchantmentRegister.getName();
                applyCost += enchantmentRegister.getAnvilApplyCostForLevel(level);
                String description = enchantmentRegister.getDescriptionForLevel(level);

                enchantmentsLore.add("&9" + name + " " + (isLoreNumbered() ? level : NumberAPI.toRoman(level)));
                enchantmentsLore.addAll(getAutoAlignLores(33, description));
            }
        }
        if (getId().endsWith("_ENCHANTED_BOOK")) {
            if (applyCost > 0) {
                enchantmentsLore.add("");
                enchantmentsLore.add("&7Apply Cost: " + "&3" + applyCost + " Exp Levels");
            }
            enchantmentsLore.add("");
            enchantmentsLore.addAll(getAutoAlignLores(33, "&7Use this on an item in an Anvil to apply it!"));
        }

        return enchantmentsLore;
    }

    private void addStatLore(List<String> loreList, String statName, double value, String color) {
        addStatLore(loreList, statName, value, color, "");
    }

    private void addStatLore(List<String> loreList, String statName, double value, String color, String suffix) {
        if (value > 0) {
            String formattedValue = NumberAPI.toStringFixed(value, 0);
            loreList.add("&7" + statName + ": " + color + "+" + formattedValue + suffix);
        }
    }

    private String buildRarityLore() {
        String rarityLore = this.tier.getNameHolder();

        if (isDungeonItem()) {
            rarityLore += " DUNGEON";
            if (this.category == Category.NONE) {
                rarityLore += " ITEM";
            }
        }

        if (!this.category.getNameHolder().isBlank()) {
            rarityLore += " " + this.category.getNameHolder();
        }

        if (isRecombobulated()) {
            ChatColor colorCode = this.tier.getUpgrade().getColor();
            String obfuscatedChar = colorCode + "" + ChatColor.BOLD + ChatColor.MAGIC + "a";
            return obfuscatedChar + " " + colorCode + ChatColor.BOLD + rarityLore + " " + obfuscatedChar;
        } else {
            return this.tier.getColor() + "&l" + rarityLore;
        }
    }

    public ItemMeta getItemMeta() {
        return this.itemStack.getItemMeta();
    }

    public ItemBuilderAPI setType(@NotNull Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public Material getType() {
        return this.itemStack.getType();
    }

    public String getTypeName() {
        return this.itemStack.getType().name();
    }

    public ItemBuilderAPI setAmount(int amount) {
        if (isInvalidItem()) return null;
        this.itemStack.setAmount(amount);
        return this;
    }

    public int getAmount() {
        if (isInvalidItem()) return -1;
        return this.itemStack.getAmount();
    }

    public ItemBuilderAPI setDamaged(int value) {
        if (isInvalidItem() || value < 0) return null;
        if (getType().getMaxDurability() == 0) {
            throw new IllegalArgumentException("The item have no durability!");
        }

        ItemMeta itemMeta = getItemMeta();
        if (itemMeta instanceof Damageable damage) {
            damage.setDamage(value);
            this.itemStack.setItemMeta(damage);
        } else {
            throw new IllegalArgumentException("The item have no durability!");
        }
        return this;
    }

    public int getDamaged() {
        if (isInvalidItem()) return -1;
        if (getType().getMaxDurability() == 0) return 0;

        ItemMeta itemMeta = getItemMeta();
        if (itemMeta instanceof Damageable damage) {
            return damage.getDamage();
        } else {
            throw new IllegalArgumentException("The item have no durability!");
        }
    }

    public ItemBuilderAPI setMaxDurability(int value) {
        if (isInvalidItem() || value < 0) return null;
        if (getType().getMaxDurability() == 0) {
            throw new IllegalArgumentException("The item have no durability!");
        }

        ItemMeta itemMeta = getItemMeta();
        if (itemMeta instanceof Damageable damage) {
            damage.setMaxDamage(value);
            this.itemStack.setItemMeta(damage);
        } else {
            throw new IllegalArgumentException("The item have no durability!");
        }
        return this;
    }

    public int getMaxDurability() {
        if (isInvalidItem()) return -1;

        if (getItemMeta().isUnbreakable()) return -1;

        if (getItemMeta() instanceof Damageable damage) {
            return damage.hasMaxDamage() ? damage.getMaxDamage() : getType().getMaxDurability();
        } else {
            throw new IllegalArgumentException("The item have no durability!");
        }
    }

    public ItemBuilderAPI setUnbreakable(boolean value) {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        itemMeta.setUnbreakable(value);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public boolean isUnbreakable() {
        if (isInvalidItem()) return false;
        return getItemMeta().isUnbreakable();
    }

    public ItemBuilderAPI setFlag(ItemFlag flag) {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        itemMeta.addItemFlags(flag);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilderAPI removeFlag(ItemFlag flag) {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        if (itemMeta.hasItemFlag(flag))
            itemMeta.removeItemFlags(flag);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilderAPI setAllFlags() {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        for (ItemFlag flag : ItemFlag.values()) {
            itemMeta.addItemFlags(flag);
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilderAPI removeAllFlags() {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        for (ItemFlag flag : ItemFlag.values()) {
            if (itemMeta.hasItemFlag(flag))
                itemMeta.removeItemFlags(flag);
        }
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilderAPI setHideTooltip(boolean value) {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        itemMeta.setHideTooltip(value);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public boolean isHideTooltip() {
        if (isInvalidItem()) return false;
        return getItemMeta().isHideTooltip();
    }

    public ItemBuilderAPI setAttribute(String attribute, double value) {
        if (isInvalidItem()) return null;
        NBT.modifyComponents(this.itemStack, nbt -> {
            ReadWriteNBTCompoundList modifiers = nbt.getCompoundList("attribute_modifiers");
            ReadWriteNBT attributeCompound = modifiers.addCompound();
            attributeCompound.setString("type", attribute);
            attributeCompound.setString("slot", "mainhand");
            attributeCompound.setString("id",UUID.randomUUID().toString());
            attributeCompound.setDouble("amount", value);
            attributeCompound.setString("operation","add_value");
        });
        return this;
    }

    public ItemBuilderAPI setPotionType(@NotNull PotionType potType) {
        if (isInvalidItem()) return null;

        if (getItemMeta() instanceof PotionMeta potionMeta) {
            potionMeta.setBasePotionType(potType);
            this.itemStack.setItemMeta(potionMeta);
        } else {
            throw new IllegalArgumentException("The item must be a potion!");
        }
        return this;
    }

    public PotionType getPotionType() {
        if (isInvalidItem()) return null;

        if (getItemMeta() instanceof PotionMeta potionMeta) {
            return potionMeta.getBasePotionType();
        } else {
            throw new IllegalArgumentException("The item must be a potion!");
        }
    }

    public int getFireWorkPower() {
        if (isInvalidItem()) return 0;

        if (getItemMeta() instanceof FireworkMeta fireworkMeta) {
            return fireworkMeta.getPower();
        } else {
            throw new IllegalArgumentException("The item must be a firework!");
        }
    }

    public ItemBuilderAPI setFireworkPower(int power) {
        if (isInvalidItem()) return null;
        if (power <= 0) return this;

        if (getItemMeta() instanceof FireworkMeta fireworkMeta) {
            fireworkMeta.setPower(power);
            this.itemStack.setItemMeta(fireworkMeta);
        } else {
            throw new IllegalArgumentException("The item must be a firework!");
        }
        return this;
    }

    public ItemBuilderAPI setItemColor(@Nullable Color color) {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        switch (itemMeta) {
            case PotionMeta potionMeta -> {
                potionMeta.setColor(color);
                this.itemStack.setItemMeta(potionMeta);
                return this;
            }
            case LeatherArmorMeta leatherArmorMeta -> {
                leatherArmorMeta.setColor(color);
                this.itemStack.setItemMeta(leatherArmorMeta);
                return this;
            }
            case FireworkEffectMeta fireworkEffectMeta -> {
                if (color != null) {
                    FireworkEffect oldEffect = fireworkEffectMeta.getEffect();
                    FireworkEffect.Builder newEffect = FireworkEffect.builder()
                            .flicker(oldEffect != null && oldEffect.hasFlicker())
                            .trail(oldEffect != null && oldEffect.hasTrail()).withColor(color);
                    if (oldEffect != null)
                        newEffect.withFade(oldEffect.getFadeColors());
                    fireworkEffectMeta.setEffect(newEffect.build());
                    this.itemStack.setItemMeta(fireworkEffectMeta);
                } else {
                    setType(Material.BARRIER);
                    this.itemStack.setItemMeta(getItemMeta());
                    setType(Material.FIREWORK_STAR);
                }
                return this;
            }
            case null, default -> throw new IllegalArgumentException(
                    "Unsupported item type. Item must be leather armor, a firework star, a potion, or a tipped arrow."
            );
        }
    }

    public ItemBuilderAPI setItemColor(int red, int green, int blue) {
        return setItemColor(Color.fromRGB(red, green, blue));
    }

    public Color getItemColor() {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        switch (itemMeta) {
            case PotionMeta potionMeta -> {
                // Handle potions
                Color potionColor = potionMeta.getColor();
                if (potionColor != null) {
                    return potionColor;
                }

                PotionType basePotionType = potionMeta.getBasePotionType();
                if (basePotionType == null) {
                    return null;
                }

                List<PotionEffect> potionEffects = basePotionType.getPotionEffects();
                Color aggregatedColor = null;
                for (PotionEffect effect : potionEffects) {
                    if (aggregatedColor == null) {
                        aggregatedColor = effect.getType().getColor();
                    } else {
                        aggregatedColor = aggregatedColor.mixColors(effect.getType().getColor());
                    }
                }
                return aggregatedColor;
            }
            case LeatherArmorMeta leatherArmorMeta -> {
                // Handle leather armor
                return leatherArmorMeta.getColor();
            }
            case FireworkEffectMeta fireworkEffectMeta -> {
                // Handle firework effects
                FireworkEffect effect = fireworkEffectMeta.getEffect();
                if (effect != null && !effect.getColors().isEmpty()) {
                    return effect.getColors().getFirst(); // Return the first color
                }
                return null;
            }
            case null, default -> throw new IllegalArgumentException(
                    "Unsupported item type. Item must be leather armor, a firework star, a potion, or a tipped arrow."
            );
        }
    }

    public ItemBuilderAPI setItemHex(String hexColor) {
        Color color = ColorAPI.hexToColor(hexColor);
        return setItemColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public String getItemHex() {
        return ColorAPI.colorToHex(getItemColor());
    }

    public ItemBuilderAPI setSkullTexture(String texture) {
        if (isInvalidItem() || getType() != Material.PLAYER_HEAD) return null;

        NBT.modifyComponents(this.itemStack, nbt -> {
            ReadWriteNBT profileNbt = nbt.getOrCreateCompound("minecraft:profile");
            profileNbt.setUUID("id", UUID.randomUUID());
            ReadWriteNBTCompoundList propertiesList = profileNbt.getCompoundList("properties");

            // Find if a 'textures' compound already exists
            ReadWriteNBT textureCompound = null;
            for (ReadWriteNBT compound : propertiesList) {
                if ("textures".equals(compound.getString("name"))) {
                    textureCompound = compound;
                    break;
                }
            }

            // If the 'textures' compound is found, update the texture, otherwise add a new one
            if (textureCompound != null) {
                textureCompound.setString("value", texture); // Update existing texture
            } else {
                ReadWriteNBT newTextureCompound = propertiesList.addCompound();
                newTextureCompound.setString("name", "textures");
                newTextureCompound.setString("value", texture);
            }
        });
        return this;
    }

    public String getSkullTexture() {
        if (isInvalidItem() || getType() != Material.PLAYER_HEAD) return null;

        return NBT.modifyComponents(this.itemStack, nbt -> {
            ReadWriteNBT profileNbt = nbt.getCompound("minecraft:profile");
            if (profileNbt == null) return "NONE"; // Return "none" if the profile is missing

            // Get the 'properties' list (no casting)
            ReadWriteNBTCompoundList propertiesList = profileNbt.getCompoundList("properties");
            if (propertiesList == null || propertiesList.isEmpty()) return "NONE"; // Return "none"
            // if a property list is empty

            // Get the first compound in the property list
            ReadWriteNBT propertiesNbt = propertiesList.get(0); // No need to cast to List<ReadWriteNBT>

            // Return the texture value or "none" if it's not found
            return propertiesNbt.getOrDefault("value", "NONE");
        });
    }

    public ItemBuilderAPI removeSkullTexture() {
        if (isInvalidItem() || getType() != Material.PLAYER_HEAD) return null;

        NBT.modifyComponents(this.itemStack, nbt -> {
            if (nbt.hasTag("minecraft:profile")) {
                nbt.removeKey("minecraft:profile");
            }
        });
        return this;
    }

    public String getDefaultName() {
        if (isInvalidItem()) return null;
        return new TranslatableComponent(this.itemStack.getTranslationKey()).toPlainText();
    }

    public ItemBuilderAPI setItemName(String name) {
        return setDisplayName(name, false);
    }

    public String getStripDisplayName() {
        return new TextAPI(getDisplayName()).stripColor().build();
    }

    public String getDisplayName() {
        if (isInvalidItem()) return null;

        return NBT.modify(this.itemStack, nbt -> {
            return nbt.getOrDefault("display_name", getDefaultName());
        });
    }

    public ItemBuilderAPI setDisplayName(String name, boolean force) {
        if (isInvalidItem()) return null;

        this.displayName = new TextAPI(name).colorRecognise().build();

        if (!force) {
            this.displayName = new TextAPI(this.displayName).stripColor().build();
            this.displayName = isRecombobulated()
                    ? new TextAPI(this.displayName).setColor(tier.getUpgrade().getColor()).build()
                    : new TextAPI(this.displayName).setColor(tier.getColor()).build();
        }
        ItemMeta itemMeta = getItemMeta();
        itemMeta.setDisplayName(this.displayName);
        itemMeta.setItemName(this.displayName);
        this.itemStack.setItemMeta(itemMeta);

        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("display_name", this.displayName);
        });
        return this;
    }

    public List<String> getLores() {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        return itemMeta.hasLore() ? new ArrayList<>(Optional.ofNullable(itemMeta.getLore()).orElse(new ArrayList<>())) : Collections.emptyList();
    }

    public ItemBuilderAPI setLores(List<String> lores) {
        if (isInvalidItem()) return null;

        List<String> modifiableLores = new ArrayList<>(lores);
        modifiableLores.replaceAll(text -> new TextAPI(text).colorRecognise().build());

        ItemMeta itemMeta = getItemMeta();
        itemMeta.setLore(modifiableLores);
        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilderAPI resetLores() {
        if (isInvalidItem()) return null;
        return setLores(new ArrayList<>());
    }

    public ItemBuilderAPI setLore(int index, String lore) {
        if (isInvalidItem() || index <= 0) return null;

        List<String> lores = new ArrayList<>(getLores());
        while (index > lores.size()) lores.add("");
        lores.set(index - 1, lore);

        return setLores(lores);
    }

    public ItemBuilderAPI removeLore(int index) {
        if (isInvalidItem()) return null;

        List<String> lores = getLores();
        if (index <= 0 || index >= lores.size()) return null;

        lores.remove(index - 1);
        return setLores(lores);
    }

    public ItemBuilderAPI moveLore(int oldIndex, int newIndex) {
        if (isInvalidItem()) return null;
        if (oldIndex == newIndex) return this;

        List<String> lores = getLores();
        if (oldIndex <= 0 || oldIndex >= lores.size()) return null;
        if (newIndex <= 0 || newIndex >= lores.size()) return null;

        String lore = lores.remove(oldIndex - 1);
        lores.add(newIndex - 1, lore);
        return setLores(lores);
    }

    public ItemBuilderAPI addLore(String lore) {
        if (isInvalidItem()) return null;

        List<String> lores = new ArrayList<>(getLores());
        lores.add(lore);

        return setLores(lores);
    }

    public ItemBuilderAPI addLores(List<String> loresToAdd) {
        if (isInvalidItem()) return null;

        List<String> lores = new ArrayList<>(getLores());
        lores.addAll(loresToAdd);

        return setLores(lores);
    }

    public ItemBuilderAPI addLore(int index, String lore) {
        if (isInvalidItem() || index <= 0) return null;

        List<String> lores = new ArrayList<>(getLores());
        if (index > lores.size()) index = lores.size();
        lores.add(index - 1, lore);

        return setLores(lores);
    }

    public String getId() {
        if (isInvalidItem()) return null;

        return NBT.modify(this.itemStack, nbt -> {
            String finalID = new TextAPI(getStripDisplayName()).convertToEnumStringFormat().build();
            if (!nbt.hasTag("id")) {
                nbt.setString("id", finalID);
            }
            return nbt.getOrDefault("id", finalID);
        });
    }

    public ItemBuilderAPI setId(String inputID) {
        if (isInvalidItem()) return null;

        String finalID = new TextAPI(inputID).convertToEnumStringFormat().build();
        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("id", finalID);
        });
        return this;
    }

    public String getDescription() {
        if (isInvalidItem()) return null;

        return NBT.modify(this.itemStack, nbt -> {
            return nbt.getOrDefault("description", "");
        });
    }

    public ItemBuilderAPI setDescription(String description) {
        if (isInvalidItem()) return null;

        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("description", description);
        });
        return this;
    }

    public List<String> getDescriptionAutoAlignLores(int maxLength) {
        return getAutoAlignLores(maxLength, getDescription());
    }

    public List<String> getAutoAlignLores(int maxLength, String description) {
        if (isInvalidItem()) return null;
        return List.of(ChatPaginator.wordWrap(new TextAPI(description).colorRecognise().build(), maxLength));
    }

    public ItemBuilderAPI setDescriptionLineLength(int value) {
        if (isInvalidItem()) return null;

        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
                nbtList.setInteger("description_cap", value);
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting description line length of the item!");
        }
        return this;
    }

    public int getDescriptionLineLength() {
        if (isInvalidItem()) return -1;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            return nbtList.getOrDefault("description_cap", 30);
        });
    }

    public Tier getTier() {
        if (isInvalidItem()) return null;
        return this.tier;
    }

    public ItemBuilderAPI setTier(Tier tier) {
        if (isInvalidItem()) return null;
        try {
            TierAPI tierAPI = new TierAPI(this.itemStack).setTier(tier);
            this.tier = tierAPI.getTier();
            this.nearestTiers = new TierAPI(this.itemStack).getNearTiersCircle();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting tier of the item!");
        }
        return this;
    }

    public List<Tier> getNearestTiers() {
        if (isInvalidItem()) return null;
        return this.nearestTiers;
    }

    public Category getCategory() {
        if (isInvalidItem()) return null;
        return this.category;
    }

    public Category getMaterialCategory() {
        if (isInvalidItem()) return null;
        return new CategoryAPI(this.itemStack).getMaterialCategory();
    }

    public List<Category> getNearestCategories() {
        if (isInvalidItem()) return null;
        return this.nearestCategories;
    }

    public ItemBuilderAPI addEnchantment(Enchantment enchantment, int level) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantments");
                for (Enchantment conflictEnchant : EnchantmentsLoader.getEnchantmentRegister(enchantment).getExclusiveEnchantments()) {
                    if (conflictEnchant.equals(enchantment)) {
                        nbtList.removeKey(enchantment.name().toLowerCase());
                    }
                }
                nbtList.setInteger(enchantment.name().toLowerCase(), level);
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while adding enchantment to the item!");
        }
        setGlowing(!getEnchantments().isEmpty());
        return this;
    }

    public ItemBuilderAPI removeEnchantment(Enchantment enchantment) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantments");
                nbtList.removeKey(enchantment.name().toLowerCase());
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while removing enchantment to the item!");
        }
        setGlowing(!getEnchantments().isEmpty());
        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        if (isInvalidItem()) return Collections.emptyMap();

        Map<Enchantment, Integer> enchantmentsMap = new HashMap<>();
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantments");
                for (String key : nbtList.getKeys()) {
                    try {
                        Enchantment enchantment = Enchantment.valueOf(key.toUpperCase());
                        enchantmentsMap.put(enchantment, nbtList.getInteger(key));
                    } catch (IllegalArgumentException e) {
                        Bukkit.getLogger().warning("Invalid enchantment key: " + key);
                    }
                }
                return null; // Return value ignored in NBT.modify context
            });
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error while getting enchantments from the item!");
        }

        // Return a sorted map using TreeMap (natural order sorts by keys alphabetically)
        return enchantmentsMap.isEmpty() ? new HashMap<>() : new TreeMap<>(enchantmentsMap);
    }

    public Integer getEnchantmentLevel(Enchantment enchantment) {
        if (isInvalidItem()) return null;
        return getEnchantments().getOrDefault(enchantment, null);
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        if (isInvalidItem()) return false;
        return getEnchantments().containsKey(enchantment);
    }

    public ItemBuilderAPI setCategory(Category category) {
        if (isInvalidItem()) return null;
        try {
            CategoryAPI categoryAPI = new CategoryAPI(this.itemStack).setCategory(category);
            this.category = categoryAPI.getCategory();
            this.nearestCategories = categoryAPI.getNearCategoriesCircle();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting category of the item!");
        }
        return this;
    }

    public ItemBuilderAPI setUnclassified(boolean value) {
        if (isInvalidItem()) return null;
        Category category = value ? Category.UNCLASSIFIED : getMaterialCategory();
        setCategory(category);
        this.category = category;
        return this;
    }

    public ItemBuilderAPI setDungeonItem(boolean value) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
                nbtList.setBoolean("dungeon_item", value);
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting dungeon item!");
        }
        return this;
    }

    public boolean isDungeonItem() {
        if (isInvalidItem()) return false;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            return nbtList.getOrDefault("dungeon_item", false);
        });
    }

    public ItemBuilderAPI setLoreNumbered(boolean value) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
                nbtList.setBoolean("lore_numbered", value);
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting lore numbered of item!");
        }
        return this;
    }

    public boolean isLoreNumbered() {
        if (isInvalidItem()) return false;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            return nbtList.getOrDefault("lore_numbered", false);
        });
    }

    public ItemBuilderAPI setGlowing(boolean value) {
        if (isInvalidItem()) return null;
        try {
            ItemMeta itemMeta = getItemMeta();
            itemMeta.setEnchantmentGlintOverride(value);
            this.itemStack.setItemMeta(itemMeta);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting item glowing!");
        }
        return this;
    }

    public boolean isGlowing() {
        if (isInvalidItem()) return false;
        return getItemMeta().getEnchantmentGlintOverride();
    }

    public ItemBuilderAPI setUnique(boolean value) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
                if (value) nbtList.setString("unique", UUID.randomUUID().toString());
                else nbtList.setString("unique", "none");
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting unique of item!");
        }
        return this;
    }

    public boolean isUnique() {
        if (isInvalidItem()) return false;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            return !String.valueOf(nbtList.getOrDefault("unique", "none")).equals("none");
        });
    }

    public ItemBuilderAPI setRecombobulated(boolean value) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
                nbtList.setBoolean("is_Recombobulated", value);
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while recombobulating item!");
        }
        return this;
    }

    public boolean isRecombobulated() {
        if (isInvalidItem()) return false;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            return nbtList.getOrDefault("is_recombobulated", false);
        });
    }

    public ItemBuilderAPI setTimestamp(boolean value) {
        if (isInvalidItem()) return null;
        try {
            NBT.modify(this.itemStack, nbt -> {
                ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
                if (value) nbtList.setLong("timestamp", System.currentTimeMillis());
                else nbtList.setLong("timestamp", -1L);
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting timestamp of item!");
        }
        return this;
    }

    public long getTimestamp() {
        if (isInvalidItem()) return -1;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            return nbtList.getOrDefault("timestamp", -1L);
        });
    }

    public ItemBuilderAPI setHealth(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("HEALTH", value);
        });
        return this;
    }

    public double getHealth() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("HEALTH", 0d);
        });
    }

    public ItemBuilderAPI setDefense(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("DEFENSE", value);
        });
        return this;
    }

    public double getDefense() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("DEFENSE", 0d);
        });
    }

    public ItemBuilderAPI setStrength(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("STRENGTH", value);
        });
        return this;
    }

    public double getStrength() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("STRENGTH", 0d);
        });
    }

    public ItemBuilderAPI setIntelligence(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("INTELLIGENCE", value);
        });
        return this;
    }

    public double getIntelligence() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("INTELLIGENCE", 0d);
        });
    }

    public ItemBuilderAPI setCritChance(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("CRITICAL_CHANCE", value);
        });
        return this;
    }

    public double getCritChance() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("CRITICAL_CHANCE", 0d);
        });
    }

    public ItemBuilderAPI setCritDamage(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("CRITICAL_DAMAGE", value);
        });
        return this;
    }

    public double getCritDamage() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("CRITICAL_DAMAGE", 0d);
        });
    }

    public ItemBuilderAPI setBonusAttackSpeed(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("BONUS_ATTACK_SPEED", value);
        });
        return this;
    }

    public double getBonusAttackSpeed() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("BONUS_ATTACK_SPEED", 0d);
        });
    }

    public ItemBuilderAPI setTrueDefense(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("TRUE_DEFENSE", value);
        });
        return this;
    }

    public double getTrueDefense() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("TRUE_DEFENSE", 0d);
        });
    }

    public ItemBuilderAPI setFerocity(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("FEROCITY", value);
        });
        return this;
    }

    public double getFerocity() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("FEROCITY", 0d);
        });
    }

    public ItemBuilderAPI setHealthRegen(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("HEALTH_REGEN", value);
        });
        return this;
    }

    public double getHealthRegen() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("HEALTH_REGEN", 0d);
        });
    }

    public ItemBuilderAPI setVitality(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("VITALITY", value);
        });
        return this;
    }

    public double getVitality() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("VITALITY", 0d);
        });
    }

    public ItemBuilderAPI setSwingRange(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            nbtList.setDouble("SWING_RANGE", value);
        });
        return this;
    }

    public double getSwingRange() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("CombatStats");
            return nbtList.getOrDefault("SWING_RANGE", 0d);
        });
    }

    public ItemBuilderAPI setMiningSpeed(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            nbtList.setDouble("MINING_SPEED", value);
        });
        return this;
    }

    public double getMiningSpeed() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            return nbtList.getOrDefault("MINING_SPEED", 0d);
        });
    }

    public ItemBuilderAPI setMiningFortune(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            nbtList.setDouble("MINING_FORTUNE", value);
        });
        return this;
    }

    public double getMiningFortune() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            return nbtList.getOrDefault("MINING_FORTUNE", 0d);
        });
    }

    public ItemBuilderAPI setMiningSpread(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            nbtList.setDouble("MINING_SPREAD", value);
        });
        return this;
    }

    public double getMiningSpread() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            return nbtList.getOrDefault("MINING_SPREAD", 0d);
        });
    }

    public ItemBuilderAPI setFarmingFortune(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            nbtList.setDouble("FARMING_FORTUNE", value);
        });
        return this;
    }

    public double getFarmingFortune() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            return nbtList.getOrDefault("FARMING_FORTUNE", 0d);
        });
    }

    public ItemBuilderAPI setForagingFortune(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            nbtList.setDouble("FORAGING_FORTUNE", value);
        });
        return this;
    }

    public double getForagingFortune() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            return nbtList.getOrDefault("FORAGING_FORTUNE", 0d);
        });
    }

    public ItemBuilderAPI setBreakingPower(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            nbtList.setDouble("BREAKING_POWER", value);
        });
        return this;
    }

    public double getBreakingPower() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("GatheringStats");
            return nbtList.getOrDefault("BREAKING_POWER", 0d);
        });
    }

    public ItemBuilderAPI setAlchemyWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("ALCHEMY_WISDOM", value);
        });
        return this;
    }

    public double getAlchemyWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("ALCHEMY_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setCarpentryWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("CARPENTRY_WISDOM", value);
        });
        return this;
    }

    public double getCarpentryWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("CARPENTRY_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setCombatWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("COMBAT_WISDOM", value);
        });
        return this;
    }

    public double getCombatWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("COMBAT_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setEnchantingWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("ENCHANTING_WISDOM", value);
        });
        return this;
    }

    public double getEnchantingWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("ENCHANTING_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setFarmingWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("FARMING_WISDOM", value);
        });
        return this;
    }

    public double getFarmingWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("FARMING_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setFishingWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("FISHING_WISDOM", value);
        });
        return this;
    }

    public double getFishingWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("FISHING_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setForagingWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("FORAGING_WISDOM", value);
        });
        return this;
    }

    public double getForagingWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("FORAGING_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setMiningWisdom(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            nbtList.setDouble("MINING_WISDOM", value);
        });
        return this;
    }

    public double getMiningWisdom() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("WisdomStats");
            return nbtList.getOrDefault("MINING_WISDOM", 0d);
        });
    }

    public ItemBuilderAPI setWalkSpeed(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("MiscStats");
            nbtList.setDouble("WALK_SPEED", value);
        });
        return this;
    }

    public double getWalkSpeed() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("MiscStats");
            return nbtList.getOrDefault("WALK_SPEED", 0d);
        });
    }

    public ItemBuilderAPI setMagicFind(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("MiscStats");
            nbtList.setDouble("MAGIC_FIND", value);
        });
        return this;
    }

    public double getMagicFind() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("MiscStats");
            return nbtList.getOrDefault("MAGIC_FIND", 0d);
        });
    }

    public ItemBuilderAPI setAbsorption(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("OtherStats");
            nbtList.setDouble("ABSORPTION", value);
        });
        return this;
    }

    public double getAbsorption() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("OtherStats");
            return nbtList.getOrDefault("ABSORPTION", 0d);
        });
    }

    public ItemBuilderAPI setDamage(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("OtherStats");
            nbtList.setDouble("DAMAGE", value);
        });
        return this;
    }

    public double getDamage() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("OtherStats");
            return nbtList.getOrDefault("DAMAGE", 0d);
        });
    }

    public ItemBuilderAPI setTrueDamage(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("OtherStats");
            nbtList.setDouble("TRUE_DAMAGE", value);
        });
        return this;
    }

    public double getTrueDamage() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("stats").getOrCreateCompound("OtherStats");
            return nbtList.getOrDefault("TRUE_DAMAGE", 0d);
        });
    }

    public ItemBuilderAPI setNPCSellPrice(double value) {
        if (isInvalidItem()) return null;
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            nbtList.setDouble("npc_sell_price", value);
        });
        return this;
    }

    public double getNPCSellPrice() {
        if (isInvalidItem()) return 0;
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            return nbtList.getOrDefault("npc_sell_price", 0d);
        });
    }

    private boolean isInvalidItem() {
        return getType() == Material.AIR;
    }
}
