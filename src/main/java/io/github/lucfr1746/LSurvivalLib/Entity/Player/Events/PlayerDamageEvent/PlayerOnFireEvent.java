package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerOnFireEvent extends Event implements Cancellable {

    public enum FireDamageCause {
        LAVA, FIRE, FIRE_TICK, CAMPFIRE, HOT_FLOOR;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    private final Player player;
    private final FireDamageCause cause;
    private double damage;

    public PlayerOnFireEvent(@NotNull Player player, @NotNull FireDamageCause cause) {
        this.isCancelled = false;

        this.player = player;
        this.cause = cause;
        this.damage = 0;
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

    public FireDamageCause getCause() {
        return this.cause;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
