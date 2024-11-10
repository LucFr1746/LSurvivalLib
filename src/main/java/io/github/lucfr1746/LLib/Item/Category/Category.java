package io.github.lucfr1746.LLib.Item.Category;

public enum Category {
    HELMET("HELMET","&8This item can be reforged!"),
    CHESTPLATE("CHESTPLATE","&8This item can be reforged!"),
    LEGGINGS("LEGGINGS","&8This item can be reforged!"),
    BOOTS("BOOTS","&8This item can be reforged!"),

    NECKLACE("NECKLACE","&8This item can be reforged!"),
    CLOAK("CLOAK","&8This item can be reforged!"),
    BELT("BELT","&8This item can be reforged!"),
    BRACELET("BRACELET","&8This item can be reforged!"),
    GLOVES("GLOVES","&8This item can be reforged!"),

    SWORD("SWORD","&8This item can be reforged!"),
    LONG_SWORD("LONGSWORD","&8This item can be reforged!"),
    BOW("BOW","&8This item can be reforged!"),
    CROSS_BOW("CROSS BOW","&8This item can be reforged!"),
    TRIDENT("TRIDENT","&8This item can be reforged!"),
    MACE("MACE","&8This item can be reforged!"),
    SHIELD("SHIELD","&8This item can be reforged!"),

    AXE("AXE","&8This item can be reforged!"),
    PICKAXE("PICKAXE","&8This item can be reforged!"),
    HOE("HOE","&8This item can be reforged!"),
    SHOVEL("SHOVEL",""),
    SHEARS("SHEARS",""),
    FISHING_ROD("FISHING ROD","&8This item can be reforged!"),

    REFORGE_STONE("REFORGE STONE",""),
    POWER_STONE("POWER STONE",""),
    ACCESSORY("ACCESSORY",""),
    COSMETIC("COSMETIC",""),
    MEMENTO("MEMENTO",""),
    BAIT("BAIT",""),
    FISHING_WEAPON("FISHING WEAPON","&8This item can be reforged!"),
    PET_ITEM("PET ITEM",""),
    PORTAL("PORTAL",""),
    ITEM("ITEM", ""),

    NONE("",""),
    UNCLASSIFIED("","");

    private final String nameHolder;
    private final String description;

    public String getNameHolder() {
        return nameHolder;
    }

    public String getDescription() {
        return description;
    }

    Category(String nameHolder, String description) {
        this.nameHolder = nameHolder;
        this.description = description;
    }
}
