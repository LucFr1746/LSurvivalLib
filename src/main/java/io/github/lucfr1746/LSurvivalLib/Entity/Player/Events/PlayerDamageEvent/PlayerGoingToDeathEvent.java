package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerGoingToDeathEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Player player;
    private String message;
    private boolean lostCoin;

    public PlayerGoingToDeathEvent(@NotNull Player player, @NotNull String message, boolean lostCoin) {
        this.player = player;
        this.message = message;
        this.lostCoin = lostCoin;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    public boolean isLostCoin() {
        return this.lostCoin;
    }

    public void setLostCoin(boolean lostCoin) {
        this.lostCoin = lostCoin;
    }
}
