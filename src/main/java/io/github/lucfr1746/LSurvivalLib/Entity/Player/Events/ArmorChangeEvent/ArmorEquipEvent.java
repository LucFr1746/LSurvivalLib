package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.ArmorChangeEvent;

import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.ArmorChangeEvent.Listener.ArmorChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArmorEquipEvent extends ArmorChangeEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public ArmorEquipEvent(@NotNull final Player who, @NotNull final ItemStack item,
                           @NotNull final EquipmentSlot slot, @NotNull final ArmorAction action) {
        super(who, item, slot, action);
    }

    public ArmorEquipEvent(@NotNull final Player who, @NotNull final ItemStack item,
                           @NotNull final EquipmentSlot slot) {
        this(who, item, slot, ArmorAction.CUSTOM);
    }

    public ArmorEquipEvent(@NotNull final Player who, @NotNull final ItemStack item) {
        this(who, item, item.getType().getEquipmentSlot());
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
