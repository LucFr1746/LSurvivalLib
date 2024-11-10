package io.github.lucfr1746.LLib.Player;

import io.github.lucfr1746.LLib.Utils.TextAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerAPI {

    private final Player player;

    public PlayerAPI(Player player) {
        this.player = player;
    }

    public PlayerAPI(HumanEntity humanEntity) {
        this.player = (Player) humanEntity;
    }

    public void addItem(ItemStack itemStack) {
        this.player.getInventory().addItem(itemStack);
    }

    public PlayerAPI sendColoredMessage(String message) {
        this.player.sendMessage(new TextAPI(message).convert().build());
        return this;
    }

    public PlayerAPI sendMessage(String message) {
        this.player.sendMessage(message);
        return this;
    }

    public PlayerAPI sendChatBorder() {
        sendMessage("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        return this;
    }

    public PlayerAPI sendChatBorder(ChatColor color) {
        sendMessage(color + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        return this;
    }

    public PlayerAPI sendChatBorder(String customColor) {
        sendColoredMessage(customColor + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        return this;
    }
}
