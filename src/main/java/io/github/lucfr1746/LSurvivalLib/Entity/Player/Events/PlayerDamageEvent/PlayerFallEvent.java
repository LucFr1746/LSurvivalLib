package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerFallEvent extends Event implements Cancellable {

    public enum FallDamageCause {
        VOID, GROUND;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    private final Player player;
    private final float fallDistance;
    private final FallDamageCause fallDamageCause;
    private double damage;

    public PlayerFallEvent(@NotNull Player player, float fallDistance, @NotNull FallDamageCause fallDamageCause) {
        this.isCancelled = false;
        this.player = player;
        this.fallDistance = fallDistance;
        this.fallDamageCause = fallDamageCause;
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

    public float getFallDistance() {
        return this.fallDistance;
    }

    public FallDamageCause getFallDamageCause() {
        return this.fallDamageCause;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
