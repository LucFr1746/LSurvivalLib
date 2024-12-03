package io.github.lucfr1746.LLib.Player.LChatInput.Input;

import io.github.lucfr1746.LLib.Player.LChatInput.Input.Enums.LInputFlag;
import io.github.lucfr1746.LLib.Player.LChatInput.Input.Enums.LInputMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class LInput {
    private final Map<LInputMessage, String> messages = new HashMap<>();
    private final EnumSet<LInputFlag> flags = EnumSet.noneOf(LInputFlag.class);
    private List<String> allowedCommands = new ArrayList<>();
    private int attempts = -1;
    private int timeout = -1;

    /**
     * Checks if the given input is valid.
     *
     * @param input the input
     * */
    public abstract boolean isValidInput(@NotNull String input);

    /**
     * Sends, if present, the message of the given type to the player.
     * The following placeholders can be used: {player}, {attempts}
     *
     * @param type the message type
     * @param player the player
     * */
    public final void sendMessage(@NotNull LInputMessage type, @NotNull Player player) {
        if (!messages.containsKey(type)) return;
        player.sendMessage(messages.get(type)
                .replace("{player}", player.getName())
                .replace("{attempts}", String.valueOf(attempts)));
    }

    /**
     * Sends, if present, the message of the given type to the player.
     * The following placeholders can be used: {player}, {attempts}, {input}
     *
     * @param type the message type
     * @param player the player
     * @param input the input
     * */
    public final void sendMessage(@NotNull LInputMessage type, @NotNull Player player, @NotNull String input) {
        if (!messages.containsKey(type)) return;
        player.sendMessage(messages.get(type)
                .replace("{player}", player.getName())
                .replace("{attempts}", String.valueOf(attempts))
                .replace("{input}", input));
    }

    /**
     * Sets the message for the given message type.
     *
     * @param type the message type
     * @param message the message
     * */
    @NotNull
    public final LInput setMessage(@NotNull LInputMessage type, @NotNull String message) {
        messages.put(type, message);
        return this;
    }

    /**
     * Checks if the given flag is enabled.
     *
     * @param flag the flag
     * @return whether the flag is or not enabled
     * */
    public final boolean hasFlag(@NotNull LInputFlag flag) {
        return flags.contains(flag);
    }

    /**
     * Sets the given flags as enabled.
     *
     * @param flags the flags
     * */
    @NotNull
    public final LInput setFlags(@NotNull LInputFlag... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return this;
    }

    /**
     * Checks if the given command is allowed to be used.
     *
     * @param command the command
     * @return whether the command is allowed or not
     * */
    public final boolean isCommandAllowed(@NotNull String command) {
        return allowedCommands.contains(command);
    }

    /**
     * Sets the allowed commands to be used.
     *
     * @param allowedCommands the commands
     * */
    @NotNull
    public final LInput setAllowedCommands(@NotNull List<String> allowedCommands) {
        this.allowedCommands = allowedCommands;
        return this;
    }

    /**
     * Sets the allowed commands to be used.
     *
     * @param allowedCommands the commands
     * */
    @NotNull
    public final LInput setAllowedCommands(@NotNull String ... allowedCommands) {
        this.allowedCommands = Arrays.asList(allowedCommands);
        return this;
    }

    /**
     * Gets the remaining number of attempts the input
     * can be given.
     *
     * @return the remaining number of attempts
     * */
    public final int getAttempts() {
        return attempts;
    }

    /**
     * Sets the remaining number of attempts the input
     * can be given.
     * Any value smaller than 0 will be interpreted
     * as infinite.
     *
     * @param attempts the number of attempts
     * */
    @NotNull
    public final LInput setAttempts(int attempts) {
        this.attempts = attempts;
        return this;
    }

    /**
     * Gets the duration after which the input prompt
     * will be cancelled.
     * This duration is in seconds. Any value smaller than
     * 0 will disable the timeout.
     *
     * @return the duration
     * */
    public final int getTimeout() {
        return timeout;
    }

    /**
     * Gets the duration after which the input prompt
     * will be cancelled.
     * This duration is in seconds. Any value smaller than
     * 0 will disable the timeout.
     *
     * @param timeout  the duration
     * */
    @NotNull
    public final LInput setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Copies the properties of this object in the given
     * object.
     *
     * @param input the object
     * */
    public final void copyTo(@NotNull LInput input) {
        messages.forEach(input::setMessage);

        input.setFlags(flags.toArray(LInputFlag[]::new));
        input.setAllowedCommands(allowedCommands.toArray(String[]::new));
        input.setAttempts(attempts);
        input.setTimeout(timeout);
    }
}
