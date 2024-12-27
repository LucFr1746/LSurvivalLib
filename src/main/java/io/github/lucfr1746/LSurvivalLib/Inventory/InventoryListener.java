package io.github.lucfr1746.LSurvivalLib.Inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryListener implements Listener {

    private final InventoryManager inventoryManager;

    public InventoryListener(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        this.inventoryManager.handleClick(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        this.inventoryManager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.inventoryManager.handleClose(event);
    }
}
