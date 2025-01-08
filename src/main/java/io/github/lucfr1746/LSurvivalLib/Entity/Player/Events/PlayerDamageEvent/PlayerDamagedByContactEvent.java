package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamagedByContactEvent extends Event implements Cancellable {

    public enum ContactDamageCause {
        CACTUS, DRIPSTONE, FALLING_DRIPSTONE, BERRY_BUSH, STING, FALLING_ANVIL, FALLING_BLOCK, FLY_INTO_WALL, OUTSIDE_BORDER, THORNS, SUFFOCATION;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    private final Player player;
    private final ContactDamageCause contactDamageCause;
    private double damage;

    public PlayerDamagedByContactEvent(@NotNull Player player, double damage, @NotNull ContactDamageCause contactDamageCause) {
        this.isCancelled = false;
        this.player = player;
        this.damage = damage;
        this.contactDamageCause = contactDamageCause;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ContactDamageCause getContactDamageCause() {
        return this.contactDamageCause;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
