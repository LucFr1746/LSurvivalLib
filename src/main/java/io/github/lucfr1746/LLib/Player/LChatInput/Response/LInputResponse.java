package io.github.lucfr1746.LLib.Player.LChatInput.Response;

import io.github.lucfr1746.LLib.Player.LChatInput.Response.Enums.LInputStatus;
import org.jetbrains.annotations.NotNull;

public record LInputResponse(LInputStatus status, String value) {
    /**
     * Returns the status of an input prompt.
     *
     * @return the status
     * */
    @NotNull
    public LInputStatus status() {
        return status;
    }

    /**
     * Returns the string value of an input prompt.
     *
     * @return the value
     * */
    @NotNull
    public String value() {
        return value;
    }
}
