package io.github.lucfr1746.LLib.Player;

import io.github.lucfr1746.LLib.Utils.TextAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class PlayerAPI {

    private final Player player;

    public PlayerAPI(Player player) {
        this.player = player;
    }

    public PlayerAPI(HumanEntity humanEntity) {
        this.player = (Player) humanEntity;
    }

    public Player getPlayer() {
        return this.player;
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

    public PlayerAPI sendSuccessMessage(String message) {
        sendColoredMessage(ChatColor.GREEN + message);
        return this;
    }

    public PlayerAPI sendWarningMessage(String message) {
        sendColoredMessage(ChatColor.YELLOW + message);
        return this;
    }

    public PlayerAPI sendErrorMessage(String message) {
        sendColoredMessage(ChatColor.RED + message);
        return this;
    }

    public PlayerAPI playSoundAtPlayerLoc(Sound sound) {
        this.player.playSound(this.player.getLocation(), sound, 1f, 1f);
        return this;
    }

    public PlayerAPI playButtonClickSound() {
        playSoundAtPlayerLoc(Sound.UI_BUTTON_CLICK);
        return this;
    }

    public PlayerAPI openInventory(Plugin plugin, Inventory inventory) {
        Bukkit.getScheduler().runTask(plugin, ()
                -> this.player.openInventory(inventory));
        return this;
    }

    public PlayerAPI openInventory(Plugin plugin, Inventory inventory, Sound openSound) {
        playSoundAtPlayerLoc(openSound);
        return openInventory(plugin, inventory);
    }
}
