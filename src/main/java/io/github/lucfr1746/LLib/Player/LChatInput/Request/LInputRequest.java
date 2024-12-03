package io.github.lucfr1746.LLib.Player.LChatInput.Request;

import io.github.lucfr1746.LLib.Player.LChatInput.Input.LInput;
import io.github.lucfr1746.LLib.Player.LChatInput.Response.LInputResponse;

import java.util.concurrent.CompletableFuture;

public class LInputRequest {

    private final CompletableFuture<LInputResponse> future = new CompletableFuture<>();
    private final LInput input;

    public LInputRequest(LInput input) {
        this.input = input;
    }

    public CompletableFuture<LInputResponse> getFuture() {
        return future;
    }

    public LInput getInput() {
        return input;
    }
}
