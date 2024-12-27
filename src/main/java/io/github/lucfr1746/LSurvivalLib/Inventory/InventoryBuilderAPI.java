package io.github.lucfr1746.LSurvivalLib.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public abstract class InventoryBuilderAPI implements InventoryHandler {

    public enum LockMode {
        ALL, // Prevents all interaction
        GUI_LOCKED,   // Prevents interaction with the GUI
        PLAYER_LOCKED, // Prevents interaction with the player inventory
    }

    private Inventory inventory;
    private final Map<Integer, InventoryButton> buttonMap = new HashMap<>();
    private LockMode lockMode = LockMode.ALL;
    private int          rows = 3;
    private int   currentPage = 1;
    private String      title = "";

    public Inventory getInventory() {
        return this.inventory;
    }

    public void addButton(int slot, InventoryButton button) {
        this.buttonMap.put(slot, button);
    }

    public Map<Integer, InventoryButton> getButtonMap() {
        return this.buttonMap;
    }

    public void setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
    }

    public LockMode getLockMode() {
        return this.lockMode;
    }

    public void setCurrentPage(int page) {
        this.currentPage = Math.max(1, page);
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setRows(int rows) {
        this.rows = Math.min(rows, 6);
        this.inventory = Bukkit.createInventory(null, rows * 9, getTitle());
    }

    public int getRows() {
        return this.rows;
    }

    public void setTitle(String title) {
        this.title = title;
        this.inventory = Bukkit.createInventory(null, getRows() * 9, title);
    }

    public String getTitle() {
        return this.title;
    }

    public void decorate(Player player) {
        this.buttonMap.forEach((slot, button) -> {
            ItemStack icon = button.getIconCreator().apply(player);
            this.inventory.setItem(slot, icon);
        });
    }

    public void updateButton(Player player, int slot, InventoryButton button) {
        this.buttonMap.put(slot, button);
        ItemStack icon = button.getIconCreator().apply(player);
        this.inventory.setItem(slot, icon);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        switch (lockMode) {
            case ALL -> {
                event.setCancelled(true);
                if (event.getClickedInventory() instanceof PlayerInventory) {
                    return;
                }
            }
            case GUI_LOCKED -> {
                if (event.getClickedInventory() instanceof PlayerInventory) {
                    return;
                }
                event.setCancelled(true);
            }
            case PLAYER_LOCKED -> {
                if (!(event.getClickedInventory() instanceof PlayerInventory)) {
                    return;
                }
                event.setCancelled(true);
            }
            default -> event.setCancelled(true);
        }

        int slot = event.getSlot();
        InventoryButton button = this.buttonMap.get(slot);
        if (button != null && button.getEventConsumer() != null) {
            button.getEventConsumer().accept(event);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        this.decorate((Player) event.getPlayer());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}
