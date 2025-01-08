package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDamagedByExplosionEvent extends Event implements Cancellable {

    public enum ExplosionDamageCause {
        CREEPER, TNT, TNT_MINECART, END_CRYSTAL, SMALL_FIREBALL, FIREBALL, FIREWORK_ROCKET, BAD_RESPAWN_POINT;
    }

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    private final Player player;
    private final Entity attacker;
    private final ExplosionDamageCause explosionDamageCause;
    private double damage;

    public PlayerDamagedByExplosionEvent(@NotNull Player player, @Nullable Entity attacker, double damage, @NotNull ExplosionDamageCause explosionDamageCause) {
        this.isCancelled = false;
        this.player = player;
        this.attacker = attacker;
        this.damage = damage;
        this.explosionDamageCause = explosionDamageCause;
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

    public Entity getAttacker() {
        return this.attacker;
    }

    public ExplosionDamageCause getExplosionDamageCause() {
        return this.explosionDamageCause;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
