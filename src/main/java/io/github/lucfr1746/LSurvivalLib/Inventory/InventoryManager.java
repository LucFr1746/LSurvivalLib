package io.github.lucfr1746.LSurvivalLib.Inventory;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();
    private static final Map<Inventory, InventoryBuilderAPI> activeInventoryBuilderAPI = new HashMap<>();

    public void openGUI(Plugin plugin, InventoryBuilderAPI gui, Player player) {
        this.registerHandledInventory(gui.getInventory(), gui);
        this.registerInventoryBuilderAPI(gui.getInventory(), gui);
        new PlayerAPI(player).openInventory(plugin, gui.getInventory());
    }

    public void registerHandledInventory(Inventory inventory, InventoryHandler handler) {
        this.activeInventories.put(inventory, handler);
    }

    public void registerInventoryBuilderAPI(Inventory inventory, InventoryBuilderAPI inventoryBuilderAPI) {
        activeInventoryBuilderAPI.put(inventory, inventoryBuilderAPI);
    }

    public void unregisterInventory(Inventory inventory) {
        this.activeInventories.remove(inventory);
    }

    public void unregisterInventoryBuilderAPI(Inventory inventory) {
        activeInventoryBuilderAPI.remove(inventory);
    }

    public InventoryBuilderAPI getActiveInventoryBuilderAPI(Inventory inventory) {
        return activeInventoryBuilderAPI.get(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onClick(event);
        }
    }

    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onOpen(event);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHandler handler = this.activeInventories.get(inventory);
        if (handler != null) {
            handler.onClose(event);
            this.unregisterInventory(inventory);
            this.unregisterInventoryBuilderAPI(inventory);
        }
    }
}
