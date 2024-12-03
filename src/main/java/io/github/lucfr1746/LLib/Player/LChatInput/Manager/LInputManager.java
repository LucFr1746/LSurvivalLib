package io.github.lucfr1746.LLib.Player.LChatInput.Manager;

import io.github.lucfr1746.LLib.Player.LChatInput.Input.Enums.LInputMessage;
import io.github.lucfr1746.LLib.Player.LChatInput.Input.LInput;
import io.github.lucfr1746.LLib.Player.LChatInput.Request.LInputRequest;
import io.github.lucfr1746.LLib.Player.LChatInput.Response.Enums.LInputStatus;
import io.github.lucfr1746.LLib.Player.LChatInput.Response.LInputResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LInputManager {

    private static JavaPlugin plugin;
    private static final Map<UUID, Queue<LInputRequest>> requestsQueue = new HashMap<>();

    /**
     * Prompts the player with the given unique id for input of a given form
     * defined by the LInput object.
     *
     * @param plugin the plugin
     * @param uuid the unique id
     * @param input the input type
     *
     * @return a CompletableFuture that returns the response to the prompt.
     * */
    public static CompletableFuture<LInputResponse> promptInput(@NotNull JavaPlugin plugin,
                                                                @NotNull UUID uuid,
                                                                @NotNull LInput input) {
        if (LInputManager.plugin == null) initialize(plugin);
        Queue<LInputRequest> queue = requestsQueue.computeIfAbsent(uuid, k -> new LinkedList<>());
        LInputRequest request = new LInputRequest(input);

        queue.add(request);
        if (queue.size() == 1)
            initializeInputRequest(uuid, request);

        return request.getFuture();
    }

    /* Initializes the tool for the hosting plugin. */
    private static void initialize(@NotNull JavaPlugin plugin) {
        LInputManager.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new LEventsListener(), plugin);
    }

    /* Initializes the new request with the timeout task and sending the prompt message. */
    private static void initializeInputRequest(@NotNull UUID uuid, @NotNull LInputRequest request) {
        /* Sending the prompt message. */
        sendInputMessage(uuid, request.getInput(), LInputMessage.PROMPT);

        /* Initializing the timeout task. */
        if (request.getInput().getTimeout() < 0) return;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!requestsQueue.containsKey(uuid)) return;
            Queue<LInputRequest> requests = requestsQueue.get(uuid);

            if (requests.element() != request) return;
            sendInputMessage(uuid, request.getInput(), LInputMessage.TIMEOUT);
            completeCurrentRequest(uuid, new LInputResponse(LInputStatus.TIMEOUT, ""));
        }, request.getInput().getTimeout() * 20L);
    }

    /* Sends the input message to the player with the given unique id if he's online. */
    private static void sendInputMessage(@NotNull UUID uuid, @NotNull LInput input, @NotNull LInputMessage message) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null) input.sendMessage(message, player);
    }

    /* Gets the current head of the input requests queue. */
    @Nullable
    protected static LInput getCurrentRequest(@NotNull UUID uuid) {
        if (!requestsQueue.containsKey(uuid)) return null;
        return requestsQueue.get(uuid).element().getInput();
    }

    /* Completes the CompletableFuture of the current head of the input requests queue. */
    protected static void completeCurrentRequest(@NotNull UUID uuid, @NotNull LInputResponse response) {
        Queue<LInputRequest> requests = requestsQueue.get(uuid);

        requests.element().getFuture().complete(response);
        requests.remove();

        if (requests.isEmpty()) requestsQueue.remove(uuid);
        else initializeInputRequest(uuid, requests.element());
    }

    /* Completes all the CompletableFuture from the input requests queue. */
    protected static void completeAllRequests(@NotNull UUID uuid, @NotNull LInputResponse response) {
        if (!requestsQueue.containsKey(uuid)) return;

        requestsQueue.get(uuid).forEach(request -> request.getFuture().complete(response));
        requestsQueue.remove(uuid);
    }

    /* Clears all the input requests without completing them. */
    protected static void clearAllRequests(@NotNull UUID uuid) {
        requestsQueue.remove(uuid);
    }
}
