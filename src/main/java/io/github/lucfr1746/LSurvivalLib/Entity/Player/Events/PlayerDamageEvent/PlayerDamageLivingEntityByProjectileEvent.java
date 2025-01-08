package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDamageLivingEntityByProjectileEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    private final Player player;
    private final Projectile projectile;
    private final LivingEntity victim;
    private double damage;

    public PlayerDamageLivingEntityByProjectileEvent(@NotNull Player player, @NotNull Projectile projectile, @NotNull LivingEntity victim, double damage) {
        this.isCancelled = false;
        this.player = player;
        this.projectile = projectile;
        this.victim = victim;
        this.damage = damage;
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

    public Projectile getProjectile() {
        return this.projectile;
    }

    public LivingEntity getVictim() {
        return this.victim;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
