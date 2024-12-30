package io.github.lucfr1746.LSurvivalLib.Entity.Player;

import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerActionBarManager {

    private final LSurvivalLib plugin;

    private final Player player;
    private final PriorityQueue<ActionBarMessage> messageQueue = new PriorityQueue<>();
    private final ConcurrentLinkedQueue<ActionBarMessage> activeMessages = new ConcurrentLinkedQueue<>();

    public PlayerActionBarManager(LSurvivalLib plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        startActionBarTask();
    }

    // Add a message to the queue
    public void addActionBarToQuery(Component component, int priority, int secondsUntilEnd) {
        ActionBarMessage message = new ActionBarMessage(component, priority, secondsUntilEnd);
        synchronized (messageQueue) {
            messageQueue.offer(message);
        }
    }

    // Task to display action bar messages
    private void startActionBarTask() {
        new BukkitRunnable() {
            private ActionBarMessage currentMessage = null;

            @Override
            public void run() {
                // Check if the current message is finished
                if (currentMessage == null || currentMessage.getSecondsRemaining() <= 0) {
                    synchronized (messageQueue) {
                        // Move the next message from the queue if available
                        if (!messageQueue.isEmpty()) {
                            currentMessage = messageQueue.poll();
                        }
                    }
                }

                // Display the current message if available
                if (currentMessage != null) {
                    sendActionBar(currentMessage.getComponent());
                    currentMessage.decreaseSecondsRemaining();
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L); // Runs every second (20 ticks)
    }

    // Sends action bar text to the player
    private void sendActionBar(Component actionBar) {
        BaseComponent[] legacyComponents = BungeeComponentSerializer.get().serialize(actionBar);
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, legacyComponents);
    }

    // Inner class for ActionBarMessage
    private static class ActionBarMessage implements Comparable<ActionBarMessage> {
        private final Component component;
        private final int priority;
        private int secondsRemaining;

        public ActionBarMessage(Component component, int priority, int secondsUntilEnd) {
            this.component = component;
            this.priority = priority;
            this.secondsRemaining = secondsUntilEnd;
        }

        public Component getComponent() {
            return component;
        }

        public int getSecondsRemaining() {
            return secondsRemaining;
        }

        public void decreaseSecondsRemaining() {
            this.secondsRemaining--;
        }

        @Override
        public int compareTo(ActionBarMessage other) {
            return Integer.compare(other.priority, this.priority); // Higher priority comes first
        }
    }
}
