package io.github.lucfr1746.LLib.Item.Enchantments;

import io.github.lucfr1746.LLib.Utils.Reflex;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.network.chat.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R1.CraftServer;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;
import java.util.function.BiConsumer;

public class EnchantmentAPI {

    private static final MinecraftServer SERVER;
    private static final Registry<Enchantment> ENCHANTMENT_REGISTRY;
    private static final String HOLDER_SET_NAMED_CONTENTS_FIELD = "c";
    private static final String HOLDER_REFERENCE_TAGS_FIELD = "b";

    static {
        SERVER = ((CraftServer) Bukkit.getServer()).getServer();
        ENCHANTMENT_REGISTRY = SERVER.registryAccess().registry(Registries.ENCHANTMENT).orElse(null);
    }

    public EnchantmentAPI() {
    }

    public EnchantmentAPI(String enchantId, String description, Set<Material> supportedMaterials, Set<Material> primaryMaterials, EquipmentSlot equipmentSlot, int weight, int maxLevel, int minCost, int maxCost, double costMultiplier, int anvilCost, boolean isCurse, boolean isTreasure, boolean isDiscoverable, boolean isTradeable) {
        createEnchantment(enchantId, description, supportedMaterials, primaryMaterials, equipmentSlot, weight, maxLevel, minCost, maxCost, costMultiplier, anvilCost, isCurse, isTreasure, isDiscoverable, isTradeable);
    }

    public EnchantmentAPI(String enchantId, String description, Set<Material> supportedMaterials, Set<Material> primaryMaterials, EquipmentSlot equipmentSlot, int weight, int maxLevel, int minCost, int maxCost, double costMultiplier, int anvilCost) {
        createEnchantment(enchantId, description, supportedMaterials, primaryMaterials, equipmentSlot, weight, maxLevel, minCost, maxCost, costMultiplier, anvilCost, false, false, false, false);
    }

    private void createEnchantment(String enchantId, String description, Set<Material> supportedMaterials, Set<Material> primaryMaterials, EquipmentSlot equipmentSlot, int weight, int maxLevel, int minCost, int maxCost, double costMultiplier, int anvilCost, boolean isCurse, boolean isTreasure, boolean isDiscoverable, boolean isTradeable) {
        Reflex.setFieldValue(ENCHANTMENT_REGISTRY, "l", false);
        Reflex.setFieldValue(ENCHANTMENT_REGISTRY, "m", new IdentityHashMap<>());

        Component component = Component.literal(description);
        HolderSet<Enchantment> exclusiveSet = HolderSet.direct();
        DataComponentMap effects = DataComponentMap.builder().build();

        HolderSet.Named<Item> supportedItems = createItemSet("enchant_supported", enchantId, supportedMaterials);
        HolderSet.Named<Item> primaryItems = createItemSet("enchant_primary", enchantId, primaryMaterials);
        net.minecraft.world.entity.EquipmentSlotGroup[] slots = convertToNmsSlots(new EquipmentSlot[]{ equipmentSlot });

        Enchantment.Cost minimumCost = new Enchantment.Cost(minCost, (int) Math.floor(costMultiplier * maxCost));
        Enchantment.Cost maximumCost = new Enchantment.Cost(maxCost, (int) Math.floor(costMultiplier * maxCost));
        Enchantment.EnchantmentDefinition definition = Enchantment.definition(supportedItems, primaryItems, weight, maxLevel, minimumCost, maximumCost, anvilCost, slots);

        Enchantment enchantment = new Enchantment(component, definition, exclusiveSet, effects);

        Holder.Reference<Enchantment> reference = ENCHANTMENT_REGISTRY.createIntrusiveHolder(enchantment);
        Registry.register(ENCHANTMENT_REGISTRY, enchantId, enchantment);

        if (isCurse) addInTag(EnchantmentTags.CURSE, reference);
        else {
            if (isTreasure) addInTag(EnchantmentTags.TREASURE, reference);
            else addInTag(EnchantmentTags.NON_TREASURE, reference);

            if (isTradeable) addInTag(EnchantmentTags.TRADEABLE, reference);
            else removeFromTag(EnchantmentTags.TRADEABLE, reference);

            if (isDiscoverable) addInTag(EnchantmentTags.IN_ENCHANTING_TABLE, reference);
            else removeFromTag(EnchantmentTags.IN_ENCHANTING_TABLE, reference);
        }

        if (ENCHANTMENT_REGISTRY.getHolder(key(enchantId)).isPresent()) ENCHANTMENT_REGISTRY.getHolder(key(enchantId)).get();
        ENCHANTMENT_REGISTRY.freeze();
    }

    private ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace(name));
    }

    @SuppressWarnings("unchecked")
    private static HolderSet.Named<Item> createItemSet(String prefix, String enchantId, Set<Material> materials) {
        Registry<Item> items = SERVER.registryAccess().registry(Registries.ITEM).orElseThrow();
        TagKey<Item> customKey = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(prefix + "/" + enchantId));
        HolderSet.Named<Item> customItems = items.getOrCreateTag(customKey);
        List<Holder<Item>> holders = new ArrayList<>();

        materials.forEach(material -> {
            ResourceLocation location = CraftNamespacedKey.toMinecraft(material.getKey());
            Holder.Reference<Item> holder = items.getHolder(location).orElse(null);
            if (holder == null) return;

            Set<TagKey<Item>> holderTags = new HashSet<>((Set<TagKey<Item>>) Objects.requireNonNull(Reflex.getFieldValue(holder, HOLDER_REFERENCE_TAGS_FIELD)));
            holderTags.add(customKey);
            Reflex.setFieldValue(holder, HOLDER_REFERENCE_TAGS_FIELD, holderTags);

            holders.add(holder);
        });

        Reflex.setFieldValue(customItems, HOLDER_SET_NAMED_CONTENTS_FIELD, holders);

        return customItems;
    }

    private net.minecraft.world.entity.EquipmentSlotGroup[] convertToNmsSlots(EquipmentSlot[] slots) {
        return Arrays.stream(slots)
                .map(slot -> CraftEquipmentSlot.getNMSGroup(slot.getGroup()))
                .toArray(net.minecraft.world.entity.EquipmentSlotGroup[]::new);
    }

    private void addInTag(TagKey<Enchantment> tagKey, Holder.Reference<Enchantment> reference) {
        modifyTag(tagKey, reference, List::add);
    }

    private void removeFromTag(TagKey<Enchantment> tagKey, Holder.Reference<Enchantment> reference) {
        modifyTag(tagKey, reference, List::remove);
    }

    private void modifyTag(TagKey<Enchantment> tagKey, Holder.Reference<Enchantment> reference, BiConsumer<List<Holder<Enchantment>>, Holder.Reference<Enchantment>> consumer) {
        HolderSet.Named<Enchantment> holders = ENCHANTMENT_REGISTRY.getTag(tagKey).orElse(null);
        if (holders == null) {
            Bukkit.getLogger().warning(tagKey + ": Could not modify HolderSet. HolderSet is NULL.");
            return;
        }
        modifyHolderSetContents(holders, reference, consumer);
    }

    @SuppressWarnings("unchecked")
    private <T> void modifyHolderSetContents(HolderSet.Named<T> holders, Holder.Reference<T> reference, BiConsumer<List<Holder<T>>, Holder.Reference<T>> consumer) {
        List<Holder<T>> contents = new ArrayList<>((List<Holder<T>>) Objects.requireNonNull(Reflex.getFieldValue(holders, HOLDER_SET_NAMED_CONTENTS_FIELD)));
        consumer.accept(contents, reference);
        Reflex.setFieldValue(holders, HOLDER_SET_NAMED_CONTENTS_FIELD, contents);
    }

    private static final String HOLDER_SET_DIRECT_CONTENTS_FIELD = "b";

    public void addExclusives(String enchantId, Set<String> exclusives) {
        Enchantment enchantment = ENCHANTMENT_REGISTRY.get(key(enchantId));
        if (enchantment == null) {
            Bukkit.getLogger().warning(enchantId + ": Could not set exclusive item list. Enchantment is not registered.");
            return;
        }

        HolderSet<Enchantment> exclusiveSet = enchantment.exclusiveSet();
        List<Holder<Enchantment>> contents = new ArrayList<>();

        exclusives.forEach(enchantIds -> {
            ResourceKey<Enchantment> key = key(enchantIds);
            ENCHANTMENT_REGISTRY.getHolder(key).ifPresent(contents::add);
        });

        Reflex.setFieldValue(exclusiveSet, HOLDER_SET_DIRECT_CONTENTS_FIELD, contents);
    }
}
