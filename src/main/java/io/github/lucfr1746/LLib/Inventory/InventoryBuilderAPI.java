package io.github.lucfr1746.LLib.Inventory;

import io.github.lucfr1746.LLib.Item.ItemBuilderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryBuilderAPI {

    private final Inventory inventory;

    public InventoryBuilderAPI(int size, String title) {
        this(null, size, title);
    }

    public InventoryBuilderAPI(InventoryHolder inventoryHolder, int size, String title) {
        this.inventory = Bukkit.createInventory(inventoryHolder, size, title);
    }

    public InventoryBuilderAPI(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory build() {
        return this.inventory;
    }

    public InventoryBuilderAPI setItemAtSlot(int slot, ItemStack itemStack) {
        this.inventory.setItem(slot, itemStack);
        return this;
    }

    public InventoryBuilderAPI setItemAtSlots(List<Integer> slots, ItemStack itemStack) {
        for (int slot : slots) {
            this.inventory.setItem(slot, itemStack);
        }
        return this;
    }

    public InventoryBuilderAPI setItemsAtSlots(List<Integer> slots, List<ItemStack> itemStacks) {
        int minSize = Math.min(slots.size(), itemStacks.size());
        for (int i = 0; i < minSize; i++) {
            this.inventory.setItem(slots.get(i), itemStacks.get(i));
        }
        return this;
    }

    public InventoryBuilderAPI setFullBackgroundOf(Material material, boolean force, boolean hideTooltip) {
        ItemStack backgroundItem = new ItemBuilderAPI(material)
                .setDisplayName(" ", true)
                .setHideTooltip(hideTooltip)
                .setUnclassified(true)
                .build();

        for (int i = 0; i < this.inventory.getSize(); i++) {
            ItemStack invItem = this.inventory.getItem(i);
            if (force || invItem == null || invItem.getType() == Material.AIR) {
                this.inventory.setItem(i, backgroundItem);
            }
        }
        return this;
    }

    public InventoryBuilderAPI setCloseButton(int slot) {
        ItemStack closeButton = new ItemBuilderAPI(Material.BARRIER)
                .setDisplayName("&cClose", true)
                .setUnclassified(true)
                .build();
        this.inventory.setItem(slot, closeButton);
        return this;
    }

    public ItemStack getItemAt(int index) {
        return this.inventory.getItem(index);
    }

    public InventoryBuilderAPI clearItems() {
        this.inventory.clear();
        return this;
    }
}
