package io.github.lucfr1746.LSurvivalLib.Entity.Player;

import io.github.lucfr1746.LSurvivalLib.Utils.APIs.TextAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    /**
     * Unequipped all non-binding-cursed armor from the player and stores them in the player's inventory.
     * <p>
     * This method checks for available space in the player's inventory before removing the armor,
     * ensuring no items are dropped or lost. Only armor not cursed with the Binding Curse enchantment
     * is unequipped and stored. If there is insufficient inventory space, the method performs no
     * action and returns false.
     *
     * @return {@code true} if the armor was successfully unequipped and stored; {@code false} otherwise.
     */
    public boolean returnPlayerArmors() {
        if (this.player == null) return false;

        // Retrieve armor items
        ItemStack[] armors = {
                this.player.getInventory().getHelmet(),
                this.player.getInventory().getChestplate(),
                this.player.getInventory().getLeggings(),
                this.player.getInventory().getBoots()
        };

        // Filter armors that are non-null and not cursed with Binding Curse
        List<ItemStack> removableArmors = Arrays.stream(armors)
                .filter(armor -> armor != null && !armor.getEnchantments().containsKey(Enchantment.BINDING_CURSE))
                .toList();

        // Check if the inventory has enough space
        if (!isEnoughSpace(removableArmors.size())) {
            return false;
        }

        // Remove valid armors from the player and add them to the inventory
        for (int i = 0; i < armors.length; i++) {
            ItemStack armor = armors[i];
            if (armor != null && !armor.getEnchantments().containsKey(Enchantment.BINDING_CURSE)) {
                switch (i) {
                    case 0 -> this.player.getInventory().setHelmet(null);
                    case 1 -> this.player.getInventory().setChestplate(null);
                    case 2 -> this.player.getInventory().setLeggings(null);
                    case 3 -> this.player.getInventory().setBoots(null);
                }
                this.player.getInventory().addItem(armor);
            }
        }

        return true;
    }

    /**
     * Checks if the player is wearing any armor piece cursed with the Binding Curse.
     * <p>
     * This method iterates through all equipped armor items and verifies if any of them
     * have the Binding Curse enchantment. If at least one cursed armor is found, the method
     * returns {@code true}. Otherwise, it returns {@code false}.
     *
     * @return {@code true} if the player is wearing at least one piece of armor with the Binding Curse enchantment; {@code false} otherwise.
     */
    public boolean isWearingBindingCurseArmor() {
        if (this.player == null) return false;

        // Retrieve armor items
        ItemStack[] armors = {
                this.player.getInventory().getHelmet(),
                this.player.getInventory().getChestplate(),
                this.player.getInventory().getLeggings(),
                this.player.getInventory().getBoots()
        };

        // Check if any armor is cursed with Binding Curse
        return Arrays.stream(armors)
                .filter(Objects::nonNull)
                .anyMatch(armor -> armor.getEnchantments().containsKey(Enchantment.BINDING_CURSE));
    }

    /**
     * Checks if the player has enough available space in their inventory to hold the specified number of items.
     * This includes both the main inventory slots and the equipment (helmet, chestplate, leggings, boots, and off-hand).
     *
     * @param spaceRequired The minimum number of available spaces required in the player's inventory.
     * @return true if the player has enough space, false otherwise.
     */
    public boolean isEnoughSpace(int spaceRequired) {
        int availableSpace = 0;

        // Check if the player has space in equipment slots (helmet, chestplate, leggings, boots, and off-hand)
        if (this.player.getInventory().getHelmet() == null) availableSpace--;
        if (this.player.getInventory().getChestplate() == null) availableSpace--;
        if (this.player.getInventory().getLeggings() == null) availableSpace--;
        if (this.player.getInventory().getBoots() == null) availableSpace--;
        if (this.player.getInventory().getItemInOffHand().getType() == Material.AIR) availableSpace--;

        // Check available space in the main inventory (excludes armor and off-hand)
        for (int i = 0; i < this.player.getInventory().getSize(); i++) {
            ItemStack item = this.player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                availableSpace++;
            }
            if (availableSpace >= spaceRequired) return true;
        }
        return false;
    }
}
