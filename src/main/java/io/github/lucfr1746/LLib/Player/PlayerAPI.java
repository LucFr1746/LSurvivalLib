package io.github.lucfr1746.LLib.Player;

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
        player.getInventory().addItem(itemStack);
    }
}
