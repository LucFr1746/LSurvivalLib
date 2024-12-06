package io.github.lucfr1746.LLib.Inventory;

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
        GUI_LOCKED   // Prevents interaction with the GUI
    }

    private final Inventory inventory;
    private final Map<Integer, InventoryButton> buttonMap = new HashMap<>();
    private LockMode lockMode = LockMode.ALL;

    public InventoryBuilderAPI() {
        this.inventory = this.createInventory();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void addButton(int slot, InventoryButton button) {
        this.buttonMap.put(slot, button);
    }

    public void setLockMode(LockMode lockMode) {
        this.lockMode = lockMode;
    }

    public LockMode getLockMode() {
        return this.lockMode;
    }

    public void decorate(Player player) {
        this.buttonMap.forEach((slot, button) -> {
            ItemStack icon = button.getIconCreator().apply(player);
            this.inventory.setItem(slot, icon);
        });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        event.setCancelled(true);
        int slot = event.getSlot();
        InventoryButton button = this.buttonMap.get(slot);

        switch (lockMode) {
            case ALL:
                event.setCancelled(true);
                break;
            case GUI_LOCKED:
                if (event.getClickedInventory() instanceof PlayerInventory) {
                    return;
                }
                event.setCancelled(true);
                break;
        }

        if (button != null) {
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

    protected abstract Inventory createInventory();
}
