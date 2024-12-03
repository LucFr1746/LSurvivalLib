package io.github.lucfr1746.LLib.Player.LChatInput.Manager;

import io.github.lucfr1746.LLib.Player.LChatInput.Input.Enums.LInputFlag;
import io.github.lucfr1746.LLib.Player.LChatInput.Input.Enums.LInputMessage;
import io.github.lucfr1746.LLib.Player.LChatInput.Input.LInput;
import io.github.lucfr1746.LLib.Player.LChatInput.Response.Enums.LInputStatus;
import io.github.lucfr1746.LLib.Player.LChatInput.Response.LInputResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class LEventsListener implements Listener {

    /* Gets the input from chat, filters it and possibly completes the input request. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LInput input = LInputManager.getCurrentRequest(player.getUniqueId());

        if (input == null) return;
        event.setCancelled(true);

        if (input.isValidInput(event.getMessage())) {
            input.sendMessage(LInputMessage.SUCCESS, player, event.getMessage());
            LInputManager.completeCurrentRequest(
                    player.getUniqueId(),
                    new LInputResponse(LInputStatus.SUCCESS, event.getMessage()));

            return;
        }

        if (input.getAttempts() > 0)
            input.setAttempts(input.getAttempts() - 1);

        if (input.getAttempts() == 0) {
            LInputManager.completeCurrentRequest(
                    player.getUniqueId(),
                    new LInputResponse(LInputStatus.FAILED_ATTEMPTS, ""));
            return;
        }

        input.sendMessage(LInputMessage.INVALID_INPUT, player);
    }

    /* Handles the case when commands are not allowed. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        LInput input = LInputManager.getCurrentRequest(player.getUniqueId());

        if (input == null) return;
        if (!input.hasFlag(LInputFlag.DISABLE_COMMANDS)) return;
        String command = event.getMessage().substring(1).split(" ")[0];

        if (input.isCommandAllowed(command)) return;
        input.sendMessage(LInputMessage.DISABLED_COMMANDS, player, command);
        event.setCancelled(true);
    }

    /* Handles the case when movement is not allowed. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        Player player = event.getPlayer();
        LInput input = LInputManager.getCurrentRequest(player.getUniqueId());

        if (input == null) return;
        if (!input.hasFlag(LInputFlag.DISABLE_MOVEMENT)) return;
        if (event.getFrom().distanceSquared(event.getTo()) == 0) return;
        input.sendMessage(LInputMessage.DISABLED_MOVEMENT, player);
        event.setCancelled(true);
    }

    /* Handles the case when interactions are not allowed. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteraction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        LInput input = LInputManager.getCurrentRequest(player.getUniqueId());

        if (input == null) return;
        if (!input.hasFlag(LInputFlag.DISABLE_INTERACTION)) return;
        input.sendMessage(LInputMessage.DISABLED_INTERACTION, player);
        event.setCancelled(true);
    }

    /* Handles the case when interactions are not allowed. */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteracted(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        LInput input = LInputManager.getCurrentRequest(player.getUniqueId());

        if (input == null) return;
        if (!input.hasFlag(LInputFlag.DISABLE_INTERACTION)) return;
        input.sendMessage(LInputMessage.DISABLED_INTERACTION, player);
        event.setCancelled(true);
    }

    /* Handles the case when there are somehow input requests for the player when he joins. */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        LInputManager.clearAllRequests(event.getPlayer().getUniqueId());
    }

    /* Clears the input requests when the player leaves the server. */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        LInputManager.completeAllRequests(
                event.getPlayer().getUniqueId(),
                new LInputResponse(LInputStatus.PLAYER_QUIT, ""));
    }

    /* Clears the input requests when the player dies. */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        LInputManager.completeAllRequests(
                event.getEntity().getUniqueId(),
                new LInputResponse(LInputStatus.PLAYER_QUIT, ""));
    }
}
