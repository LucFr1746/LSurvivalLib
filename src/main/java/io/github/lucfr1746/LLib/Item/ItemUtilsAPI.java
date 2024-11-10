package io.github.lucfr1746.LLib.Item;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemUtilsAPI {

    private final @NotNull ItemStack itemStack;

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    public ItemUtilsAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemUtilsAPI setDungeonItem(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("dungeon_item", value);
        });
        return this;
    }

    public boolean isDungeonItem() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("dungeon_item")) {
                nbtList.setBoolean("dungeon_item", false);
            }
            return nbtList.getBoolean("dungeon_item");
        });
    }

    public ItemUtilsAPI setLoreNumbered(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("lore_numbered", value);
        });
        return this;
    }

    public boolean isLoreNumbered() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("lore_numbered")) {
                nbtList.setBoolean("lore_numbered", false);
            }
            return nbtList.getBoolean("lore_numbered");
        });
    }

    public ItemUtilsAPI setDescriptionLineLength(int value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setInteger("description_cap", value);
        });
        return this;
    }

    public int getDescriptionLineLength() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("description_cap")) {
                nbtList.setInteger("description_cap", 30);
            }
            return nbtList.getInteger("description_cap");
        });
    }

    public ItemUtilsAPI setGlowing(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("glowing", value);
        });
        return this;
    }

    public boolean isGlowing() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("glowing")) {
                nbtList.setBoolean("glowing", false);
            }
            return nbtList.getBoolean("glowing");
        });
    }

    public ItemUtilsAPI setUnique(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (value) nbtList.setString("unique", UUID.randomUUID().toString());
            else nbtList.setString("unique", "none");
        });
        return this;
    }

    public boolean isUnique() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("unique")) {
                nbtList.setString("unique", "none");
                return false;
            }
            return !Objects.equals(nbtList.getString("unique"), "none");
        });
    }

    public ItemUtilsAPI setRecombobulated(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            nbtList.setBoolean("is_Recombobulated", value);
        });
        return this;
    }

    public boolean isRecombobulated() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("utils");
            if (!nbtList.hasTag("is_Recombobulated")) {
                nbtList.setBoolean("is_recombobulated", false);
            }
            return nbtList.getBoolean("is_recombobulated");
        });
    }

    public ItemUtilsAPI setTimestamp(boolean value) {
        NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (value) nbtList.setLong("timestamp", System.currentTimeMillis());
            else nbtList.setLong("timestamp", -1L);
        });
        return this;
    }

    public Long getTimestamp() {
        return NBT.modify(this.itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (!nbtList.hasTag("timestamp")) {
                nbtList.setLong("timestamp", -1L);
            }
            return nbtList.getLong("timestamp");
        });
    }

    /**
     * Creates a Set of all available Materials
     *
     * @return Set of all Materials
     */
    public Set<Material> getAllMaterials() {
        Material[] allMat = Material.values();
        return new HashSet<>(Arrays.asList(allMat));
    }

    /**
     * Creates a Set of Materials containing String
     *
     * @param arg  Set of Strings to search
     * @return Set of matching Materials
     * ex: getMaterialsContaining(Set.of("_SWORD", "_AXE"));
     */
    public Set<Material> getMaterialsContaining(Set<String> arg) {
        Set<Material> material = new HashSet<>();
        Material[] allMat = Material.values();

        for (String s : arg) {
            for (Material value : allMat) {
                if (value.toString().toLowerCase().contains(s.toLowerCase())) {
                    material.add(value);
                }
            }
        }
        return material;
    }
}
