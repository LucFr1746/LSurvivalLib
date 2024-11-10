package io.github.lucfr1746.LLib.Item;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.lucfr1746.LLib.Item.Category.Category;
import io.github.lucfr1746.LLib.Item.Category.CategoryAPI;
import io.github.lucfr1746.LLib.Item.Tier.Tier;
import io.github.lucfr1746.LLib.Item.Tier.TierAPI;
import io.github.lucfr1746.LLib.Utils.TextAPI;
import io.github.lucfr1746.LLib.Utils.UtilsAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.ChatPaginator;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilderAPI {

    private final ItemStack itemStack;
    private String displayName;
    private String internalName;
    private String skullTexture;
    private String description;

    private Tier tier;
    private List<Tier> nearestTiers;

    private Category category;
    private Category defaultCategory;
    private List<Category> nearestCategories;

    private boolean isDungeonItem;
    private boolean isLoreNumbered;
    private boolean isGlowing;
    private boolean isUnique;
    private boolean isRecombobulated;
    private Long timestamp;

    public ItemBuilderAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        initializeItem();
    }

    public ItemBuilderAPI(@NotNull Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilderAPI() {
        this(new ItemStack(Material.AIR));
    }

    private void initializeItem() {
        loadBasicProperties();
        loadTierData();
        loadCategoryData();
        loadItemUtilsData();
    }

    private void loadBasicProperties() {
        this.displayName = getDisplayName();
        this.internalName = getInternalName();
        this.skullTexture = getSkullTexture();
        this.description = getDescription();
    }

    private void loadTierData() {
        TierAPI tierAPI = new TierAPI(itemStack);
        this.tier = tierAPI.getTier();
        this.nearestTiers = tierAPI.getNearTiersCircle();
    }

    private void loadCategoryData() {
        CategoryAPI categoryAPI = new CategoryAPI(itemStack);
        this.category = categoryAPI.getCategory();
        this.defaultCategory = categoryAPI.getDefaultCategory();
        this.nearestCategories = categoryAPI.getNearCategoriesCircle();
    }

    private void loadItemUtilsData() {
        ItemUtilsAPI itemUtilsAPI = new ItemUtilsAPI(itemStack);
        this.isDungeonItem = itemUtilsAPI.isDungeonItem();
        this.isLoreNumbered = itemUtilsAPI.isLoreNumbered();
        this.isGlowing = itemUtilsAPI.isGlowing();
        this.isUnique = itemUtilsAPI.isUnique();
        this.isRecombobulated = itemUtilsAPI.isRecombobulated();
        this.timestamp = itemUtilsAPI.getTimestamp();
    }

    public @NotNull ItemStack build() {
        if (this.category == Category.UNCLASSIFIED) return this.itemStack;

        setDisplayName(this.displayName);

        List<String> finalLores = new ArrayList<>();
        if (getType().name().startsWith("LEATHER")) finalLores.add("&7Color: " + getLeatherArmorColor());

    // Category description
        if (!finalLores.isEmpty()) finalLores.add("");
        String categoryDes = this.category.getDescription();
        if (!categoryDes.isBlank()) finalLores.add(categoryDes);
    // Rarity lore
        String rarityLore = this.tier.getNameHolder() + (this.isDungeonItem ? " DUNGEON" + (this.category == Category.NONE ? " ITEM" : "") : "");
        rarityLore += this.category.getNameHolder().isBlank() ? "" : " " + this.category.getNameHolder();
        if (this.isRecombobulated) {
            ChatColor colorCode = this.tier.getUpgrade().getColor();
            String obfuscatedChar = colorCode + "" + ChatColor.BOLD + ChatColor.MAGIC + "a";
            rarityLore = obfuscatedChar + " " + colorCode + ChatColor.BOLD + rarityLore + " " + obfuscatedChar;
        } else {
            rarityLore = this.tier.getColor() + "&l" + rarityLore;
        }
        finalLores.add(rarityLore);

        return setLores(finalLores).setAllFlags().itemStack;
    }

    public @NotNull ItemMeta getItemMeta() {
        return Objects.requireNonNull(this.itemStack.getItemMeta(), "ItemMeta cannot be null!");
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
        if (isInvalidItem()) return 0;

        ItemMeta itemMeta = getItemMeta();
        if (itemMeta.isUnbreakable()) return -1;

        if (itemMeta instanceof Damageable damage) {
            return damage.getMaxDamage();
        } else {
            throw new IllegalArgumentException("The item have no durability!");
        }
    }

    public ItemBuilderAPI setUnbreakable(boolean value) {
        if (isInvalidItem()) return null;
        getItemMeta().setUnbreakable(value);
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

    public ItemBuilderAPI setPotionType(@NotNull PotionType potType) {
        if (isInvalidItem()) return null;

        PotionMeta potionMeta = (PotionMeta) getItemMeta();
        potionMeta.setBasePotionType(potType);
        this.itemStack.setItemMeta(potionMeta);
        return this;
    }

    public PotionType getPotionType() {
        return ((PotionMeta) getItemMeta()).getBasePotionType();
    }

    public ItemBuilderAPI setLeatherArmorColor(String hexColor) {
        if (isInvalidItem()) return null;

        if (getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(new UtilsAPI().hexToColor(hexColor));
            this.itemStack.setItemMeta(leatherArmorMeta);
        } else {
            throw new IllegalArgumentException("The item must be a leather armor piece");
        }
        return this;
    }

    public String getLeatherArmorColor() {
        if (isInvalidItem()) return null;

        if (getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) {
            return new UtilsAPI().colorToHex(leatherArmorMeta.getColor());
        } else {
            throw new IllegalArgumentException("The item must be a leather armor piece");
        }
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
            this.skullTexture = texture;
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
            if (propertiesList == null || propertiesList.isEmpty()) return "NONE"; // Return "none" if properties list is empty

            // Get the first compound in the properties list
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

    public ItemBuilderAPI setDisplayName(String name, boolean force) {
        if (isInvalidItem()) return null;

        this.displayName = new TextAPI(name).colorRecognise().build();

        if (!force) {
            this.displayName = new TextAPI(this.displayName).stripColor().build();
            this.displayName = isRecombobulated
                    ? new TextAPI(this.displayName).setColor(tier.getUpgrade().getColor()).build()
                    : new TextAPI(this.displayName).setColor(tier.getColor()).build();
        }
        ItemMeta itemMeta = getItemMeta();
        itemMeta.setDisplayName(this.displayName);
        this.itemStack.setItemMeta(itemMeta);

        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("display name", this.displayName);
        });
        return this;
    }

    public ItemBuilderAPI setDisplayName(String displayName) {
        return setDisplayName(displayName, false);
    }

    public String getMaterialName() {
        if (isInvalidItem()) return null;

        return ChatColor.translateAlternateColorCodes('&', "&f" +
                Arrays.stream(getType().name().toLowerCase().split("_"))
                        .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                        .collect(Collectors.joining(" "))
        );
    }

    public String getDisplayName() {
        if (isInvalidItem()) return null;

        return NBT.modify(this.itemStack, nbt -> {
            return nbt.getOrDefault("display name", getMaterialName());
        });
    }

    public String getStripDisplayName() {
        return new TextAPI(getDisplayName()).stripColor().build();
    }

    public ItemBuilderAPI setInternalName(String internalName) {
        if (isInvalidItem()) return null;

        internalName = internalName.toUpperCase().replaceAll(" ", "_");

        String finalInternalName = internalName;
        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("internal_name", finalInternalName);
        });
        this.internalName = internalName;
        return this;
    }

    public String getInternalName() {
        if (isInvalidItem()) return null;

        return NBT.modify(this.itemStack, nbt -> {
            if (!nbt.hasTag("internal_name")) {
                nbt.setString("internal_name", "NONE");
            }
            return nbt.getOrDefault("internal_name", "NONE");
        });
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

    public List<String> getLores() {
        if (isInvalidItem()) return null;

        ItemMeta itemMeta = getItemMeta();
        return itemMeta.hasLore() ? new ArrayList<>(Optional.ofNullable(itemMeta.getLore()).orElse(new ArrayList<>())) : Collections.emptyList();
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

    public ItemBuilderAPI setDescription(String description) {
        if (isInvalidItem()) return null;

        NBT.modify(this.itemStack, nbt -> {
            nbt.setString("description", description);
        });
        return this;
    }

    public String getDescription() {
        if (isInvalidItem()) return null;

        NBT.modify(this.itemStack, nbt -> {
            nbt.getOrDefault("description", "");
        });
        return "";
    }

    public List<String> getDescriptionAutoAlignLores(int maxLength) {
        if (isInvalidItem()) return null;

        return Arrays.asList(ChatPaginator.wordWrap(new TextAPI(this.description).colorRecognise().build(), maxLength));
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

    public Tier getTier() {
        if (isInvalidItem()) return null;
        return this.tier;
    }

    public List<Tier> getNearestTiers() {
        if (isInvalidItem()) return null;
        return this.nearestTiers;
    }

    public ItemBuilderAPI setCategory(Category category) {
        if (isInvalidItem()) return null;
        try {
            CategoryAPI categoryAPI = new CategoryAPI(this.itemStack).setCategory(category);
            this.category = categoryAPI.getCategory();
            this.defaultCategory = categoryAPI.getDefaultCategory();
            this.nearestCategories = categoryAPI.getNearCategoriesCircle();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting category of the item!");
        }
        return this;
    }

    public ItemBuilderAPI setUnclassified(boolean value) {
        if (isInvalidItem()) return null;
        setCategory(value ? Category.UNCLASSIFIED : getDefaultCategory());
        this.category = value ? Category.UNCLASSIFIED : getDefaultCategory();
        return this;
    }

    public Category getCategory() {
        if (isInvalidItem()) return null;
        return this.category;
    }

    public Category getDefaultCategory() {
        if (isInvalidItem()) return null;
        return this.defaultCategory;
    }

    public List<Category> getNearestCategories() {
        if (isInvalidItem()) return null;
        return this.nearestCategories;
    }

    public ItemBuilderAPI setDungeonItem(boolean value) {
        if (isInvalidItem()) return null;
        try {
            new ItemUtilsAPI(this.itemStack).setDungeonItem(value);
            this.isDungeonItem = value;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting dungeon item!");
        }
        return this;
    }

    public boolean isDungeonItem() {
        if (isInvalidItem()) return false;
        return this.isDungeonItem;
    }

    public ItemBuilderAPI setLoreNumbered(boolean value) {
        if (isInvalidItem()) return null;
        try {
            new ItemUtilsAPI(this.itemStack).setLoreNumbered(value);
            this.isLoreNumbered = value;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting lore numbered of item!");
        }
        return this;
    }

    public boolean isLoreNumbered() {
        if (isInvalidItem()) return false;
        return this.isLoreNumbered;
    }

    public ItemBuilderAPI setGlowing(boolean value) {
        if (isInvalidItem()) return null;
        try {
            new ItemUtilsAPI(this.itemStack).setGlowing(value);
            this.isGlowing = value;

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
        return this.isGlowing;
    }

    public ItemBuilderAPI setUnique(boolean value) {
        if (isInvalidItem()) return null;
        try {
            new ItemUtilsAPI(this.itemStack).setUnique(value);
            this.isUnique = value;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting unique of item!");
        }
        return this;
    }

    public boolean isUnique() {
        if (isInvalidItem()) return false;
        return this.isUnique;
    }

    public ItemBuilderAPI setRecombobulated(boolean value) {
        if (isInvalidItem()) return null;
        try {
            new ItemUtilsAPI(this.itemStack).setRecombobulated(value);
            this.isRecombobulated = value;
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while recombobulating item!");
        }
        return this;
    }

    public boolean isRecombobulated() {
        if (isInvalidItem()) return false;
        return this.isRecombobulated;
    }

    public ItemBuilderAPI setTimestamp(boolean value) {
        if (isInvalidItem()) return null;
        try {
            ItemUtilsAPI itemUtilsAPI = new ItemUtilsAPI(this.itemStack).setTimestamp(value);
            this.timestamp = itemUtilsAPI.getTimestamp();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error while setting timestamp of item!");
        }
        return this;
    }

    public long getTimestamp() {
        if (isInvalidItem()) return -1;
        return this.timestamp;
    }

    private boolean isInvalidItem() {
        return this.itemStack == null || getType() == Material.AIR;
    }
}
