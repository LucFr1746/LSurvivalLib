package io.github.lucfr1746.LLib.Item.Tier;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TierAPI {

    private final ItemStack itemStack;
    private final Tier currentTier;

    public TierAPI(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.currentTier = getTier();
    }

    public TierAPI setTier(@NotNull Tier tier) {
        if (this.itemStack.getType() == Material.AIR) return this;
        NBT.modify(itemStack, nbt -> {
            nbt.getOrCreateCompound("ExtraAttributes").setString("tier", tier.toString());
        });
        return this;
    }

    public Tier getTier() {
        return NBT.modify(itemStack, nbt -> {
            ReadWriteNBT nbtList = nbt.getOrCreateCompound("ExtraAttributes");
            if (!nbtList.hasTag("tier")) {
                nbtList.setString("tier", Tier.COMMON.name());
            }
            try {
                return Tier.valueOf(nbtList.getString("tier"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
    }

    public List<Tier> getNearTiersCircle() {
        List<Tier> result = new ArrayList<>();

        Tier frontTier;
        if (this.currentTier == Tier.COMMON) frontTier = Tier.SPECIAL;
        else frontTier = this.currentTier.getDowngrade();

        Tier backTier;
        if (this.currentTier == Tier.SPECIAL) backTier = Tier.COMMON;
        else backTier = this.currentTier.getUpgrade();

        result.add(frontTier);
        result.add(this.currentTier);
        result.add(backTier);

        return result;
    }
}
