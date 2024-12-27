package io.github.lucfr1746.LSurvivalLib.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class InventoryButton {

    private Function<Player, ItemStack> iconCreator;
    private Consumer<InventoryClickEvent> eventConsumer;
    private String buttonName;

    public InventoryButton creator(Function<Player, ItemStack> iconCreator) {
        this.iconCreator = iconCreator;
        return this;
    }

    public InventoryButton consumer(Consumer<InventoryClickEvent> eventConsumer) {
        this.eventConsumer = eventConsumer;
        return this;
    }

    public InventoryButton name(String buttonName) {
        this.buttonName = buttonName;
        return this;
    }

    public Consumer<InventoryClickEvent> getEventConsumer() {
        return this.eventConsumer;
    }

    public Function<Player, ItemStack> getIconCreator() {
        return this.iconCreator;
    }

    public String getButtonName() {
        return this.buttonName;
    }
}
