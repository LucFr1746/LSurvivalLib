package io.github.lucfr1746.LSurvivalLib.ItemStack.Category;

public enum Category {
    HELMET("HELMET",true),
    CHESTPLATE("CHESTPLATE",true),
    LEGGINGS("LEGGINGS",true),
    BOOTS("BOOTS",true),

    NECKLACE("NECKLACE",true),
    CLOAK("CLOAK",true),
    BELT("BELT",true),
    BRACELET("BRACELET",true),
    GLOVES("GLOVES",true),

    SWORD("SWORD",true),
    LONG_SWORD("LONGSWORD",true),
    BOW("BOW",true),
    CROSS_BOW("CROSS BOW",true),
    TRIDENT("TRIDENT",true),
    MACE("MACE",true),
    SHIELD("SHIELD",true),

    AXE("AXE",true),
    PICKAXE("PICKAXE",true),
    HOE("HOE",true),
    SHOVEL("SHOVEL",false),
    SHEARS("SHEARS",false),
    FISHING_ROD("FISHING ROD",true),

    REFORGE_STONE("REFORGE STONE",false),
    POWER_STONE("POWER STONE",false),
    ACCESSORY("ACCESSORY",false),
    COSMETIC("COSMETIC",false),
    MEMENTO("MEMENTO",false),
    BAIT("BAIT",false),
    FISHING_WEAPON("FISHING WEAPON",true),
    PET_ITEM("PET ITEM",false),
    PORTAL("PORTAL",false),
    ITEM("ITEM", false),
    ARROW("ARROW", false),

    NONE("",false),
    UNCLASSIFIED("",false);

    private final String nameHolder;
    private final boolean canBeReforged;

    public String getNameHolder() {
        return nameHolder;
    }

    public boolean canBeReforged() {
        return canBeReforged;
    }

    Category(String nameHolder, boolean canBeReforged) {
        this.nameHolder = nameHolder;
        this.canBeReforged = canBeReforged;
    }
}
