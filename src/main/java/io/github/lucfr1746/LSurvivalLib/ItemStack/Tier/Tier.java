package io.github.lucfr1746.LSurvivalLib.ItemStack.Tier;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Tier {
    COMMON("COMMON", ChatColor.WHITE),
    UNCOMMON("UNCOMMON", ChatColor.GREEN),
    RARE("RARE", ChatColor.BLUE),
    EPIC("EPIC", ChatColor.DARK_PURPLE),
    LEGENDARY("LEGENDARY", ChatColor.GOLD),
    MYTHIC("MYTHIC", ChatColor.LIGHT_PURPLE),
    DIVINE("DIVINE", ChatColor.AQUA),
    SPECIAL("SPECIAL", ChatColor.RED),
    VERY_SPECIAL("VERY SPECIAL", ChatColor.RED);

    private final String nameHolder;
    private final ChatColor color;

    private static final List<Tier> ordered;

    static {
        ordered = new ArrayList<>(Arrays.asList(COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL));
    }

    public String getNameHolder() {
        return nameHolder;
    }

    public ChatColor getColor() {
        return color;
    }

    public Tier getUpgrade() {
        return ordered.get(Math.min(ordinal() + 1, values().length - 1));
    }

    public Tier getDowngrade() {
        if (ordinal() - 1 < 0) return null;
        return ordered.get(ordinal() - 1);
    }

    Tier(String nameHolder, ChatColor color) {
        this.nameHolder = nameHolder;
        this.color = color;
    }
}
