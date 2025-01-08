package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import com.google.common.collect.ImmutableSet;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.Entity.Entity.EntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamageLivingEntityByMeleeEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamageLivingEntityByProjectileEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Statistic;
import io.github.lucfr1746.LSurvivalLib.ItemStack.Category.Category;
import io.github.lucfr1746.LSurvivalLib.ItemStack.ItemBuilderAPI;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;
import io.github.lucfr1746.LSurvivalLib.Utils.APIs.DamageIndicatorAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerDamageLivingEntityListenerOld implements Listener {

    private final LSurvivalLib plugin;

    public PlayerDamageLivingEntityListenerOld(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamageLivingEntityByProjectile(PlayerDamageLivingEntityByProjectileEvent event) {
        Player player = event.getPlayer();
        LivingEntity victim = event.getVictim();
        PlayerStatistic attackerStats = new PlayerStatistic(player);
        double damage = (attackerStats.getDamage() + new EntityStatistic(event.getProjectile()).getDamage())
                * (1 + attackerStats.getStrength() / 100)
                * getAdditiveMultiplier(player, victim)
                * getMultiplicativeMultiplier()
                + getBonusModifiers();

        ItemBuilderAPI itemBuilderAPI = new ItemBuilderAPI(player.getInventory().getItemInMainHand());
        boolean correctWeapon= itemBuilderAPI.getCategory() == Category.BOW || itemBuilderAPI.getCategory() == Category.CROSS_BOW || itemBuilderAPI.getCategory() == Category.TRIDENT;

        if (!correctWeapon) {
            victim.damage(Statistic.OtherStats.DAMAGE.getBaseValue());
            new DamageIndicatorAPI(plugin, event.getDamage(), victim.getLocation(),  DamageIndicatorAPI.DamageCause.NORMAL);
            return;
        }

        boolean isCrit = ThreadLocalRandom.current().nextDouble() <= (attackerStats.getCritChance() / 100);
        if (isCrit) {
            damage *= (1 + attackerStats.getCritDamage() / 100);
        }

        LivingEntityStatistic livingEntityStats = new LivingEntityStatistic(victim);
        damage *= (1 - livingEntityStats.getDefense() / (livingEntityStats.getDefense() + 100));

        event.setDamage(damage);

        victim.damage(event.getDamage());
        new DamageIndicatorAPI(plugin, event.getDamage(), victim.getLocation(),
                isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamageLivingEntityByMelee(PlayerDamageLivingEntityByMeleeEvent event) {
        Player player = event.getPlayer();
        LivingEntity victim = event.getVictim();

        PlayerStatistic attackerStats = new PlayerStatistic(player);

        ItemBuilderAPI attackedWeapon = new ItemBuilderAPI(player.getInventory().getItemInMainHand());
        boolean correctWeapon= attackedWeapon.getCategory() == Category.SWORD ||
                attackedWeapon.getCategory() == Category.LONG_SWORD ||
                attackedWeapon.getCategory() == Category.FISHING_WEAPON ||
                attackedWeapon.getCategory() == Category.PICKAXE ||
                attackedWeapon.getCategory() == Category.AXE ||
                attackedWeapon.getCategory() == Category.MACE;

//        if (!correctWeapon) {
//            event.setDamage(Statistic.OtherStats.DAMAGE.getBaseValue());
//            victim.damage(event.getDamage());
//            new DamageIndicatorAPI(plugin, event.getDamage(), victim.getLocation(),  DamageIndicatorAPI.DamageCause.NORMAL);
//            return;
//        }

        double ferocity = attackerStats.getFerocity();
        int extraAttacks = (int) Math.floor(ferocity / 100);
        if (ThreadLocalRandom.current().nextDouble() <= (ferocity % 100) / 100) extraAttacks++;

        int finalExtraAttacks = extraAttacks;
        final float attackCooldown = player.getAttackCooldown();
        new BukkitRunnable() {
            private int attackCount = 0;

            @Override
            public void run() {
                if (attackCount > finalExtraAttacks) {
                    cancel();
                    return;
                }

                double damage = attackerStats.getDamage()
                        * (1 + attackerStats.getStrength() / 100)
                        * getAdditiveMultiplier(player, victim)
                        * getMultiplicativeMultiplier()
                        + getBonusModifiers();

                boolean isCrit = ThreadLocalRandom.current().nextDouble() <= (attackerStats.getCritChance() / 100);
                if (isCrit) {
                    damage *= (1 + attackerStats.getCritDamage() / 100);
                    if (attackedWeapon.hasEnchantment(Enchantment.CRITICAL)) {
                        int level = attackedWeapon.getEnchantmentLevel(Enchantment.CRITICAL);
                        switch (level) {
                            case 1 -> damage *= (1 + (double) 10/100);
                            case 2 -> damage *= (1 + (double) 20/100);
                            case 3 -> damage *= (1 + (double) 30/100);
                            case 4 -> damage *= (1 + (double) 40/100);
                            case 5 -> damage *= (1 + (double) 50/100);
                            case 6 -> damage *= (1 + (double) 70/100);
                            case 7 -> damage *= (1 + (double) 100/100);
                            default -> damage *= (1 + (double) (level * 30 - 110)/100);
                        }
                    }
                }

                damage *= attackCooldown;

                LivingEntityStatistic livingEntityStats = new LivingEntityStatistic(victim);
                damage *= (1 - livingEntityStats.getDefense() / (livingEntityStats.getDefense() + 100));

                event.setDamage(damage);

                if (attackedWeapon.hasEnchantment(Enchantment.FIRST_STRIKE)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.FIRST_STRIKE);
                    double percentDamage = level * 0.25;
                    boolean isFirstHit = NBT.modifyPersistentData(victim, nbt -> {
                        ReadWriteNBT first_strike = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.FIRST_STRIKE.name());
                        if (!first_strike.hasTag(player.getUniqueId().toString())) {
                            first_strike.setBoolean(player.getUniqueId().toString(), false);
                            return true;
                        }
                        return first_strike.getBoolean(player.getUniqueId().toString());
                    });
                    if (isFirstHit) {
                        event.setDamage(event.getDamage() * (1 + percentDamage));
                    }
                }

                if (attackedWeapon.hasEnchantment(Enchantment.TRIPLE_STRIKE)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.TRIPLE_STRIKE);
                    double percentDamage = level * 0.10;
                    boolean isFirstThreeHit = NBT.modifyPersistentData(victim, nbt -> {
                        ReadWriteNBT triple_strike = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.TRIPLE_STRIKE.name());
                        if (!triple_strike.hasTag(player.getUniqueId().toString())) {
                            triple_strike.setInteger(player.getUniqueId().toString(), 0);
                            return true;
                        }
                        triple_strike.setInteger(player.getUniqueId().toString(), triple_strike.getInteger(player.getUniqueId().toString()) + 1);
                        return !(triple_strike.getInteger(player.getUniqueId().toString()) >= 3);
                    });
                    if (isFirstThreeHit) {
                        event.setDamage(event.getDamage() * (1 + percentDamage));
                    }
                }

                if (attackedWeapon.hasEnchantment(Enchantment.GIANT_KILLER)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.GIANT_KILLER);
                    double damageDealt;
                    double upto;
                    switch (level) {
                        case 1,2,3,4 -> {
                            damageDealt = level * 0.1;
                            upto = level * 5;
                        }
                        case 5 -> {
                            damageDealt = 0.6;
                            upto = 30;
                        }
                        case 6 -> {
                            damageDealt = 0.9;
                            upto = 45;
                        }
                        case 7 -> {
                            damageDealt = 1.2;
                            upto = 65;
                        }
                        default -> {
                            damageDealt = level * 0.3 - 0.9;
                            upto = level * 20 - 70;
                        }
                    }

                    double percentHealthAbove = Math.min((((livingEntityStats.getHealth() - attackerStats.getHealth()) / attackerStats.getHealth() * 100) * damageDealt), upto);
                    event.setDamage(event.getDamage() * (1 + percentHealthAbove / 100));
                }

                if (attackedWeapon.hasEnchantment(Enchantment.LETHALITY)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.LETHALITY);
                    double reducePercent;
                    switch (level) {
                        case 1,2,3,4,5 -> reducePercent = level * 1.2;
                        default -> reducePercent = level * 3 - 9;
                    }
                    int stackTime = NBT.modifyPersistentData(victim, nbt -> {
                        ReadWriteNBT lethality = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.LETHALITY.name());
                        if (!lethality.hasTag(player.getUniqueId().toString())) {
                            lethality.setInteger(player.getUniqueId().toString(), 0);
                        }
                        lethality.setInteger(player.getUniqueId().toString(), lethality.getInteger(player.getUniqueId().toString()) + 1);
                        return lethality.getInteger(player.getUniqueId().toString());
                    });
                    livingEntityStats.setDefense(livingEntityStats.getDefense() - (livingEntityStats.getDefense() * ((reducePercent * stackTime)/100)));
                }

                if (attackedWeapon.hasEnchantment(Enchantment.PROSECUTE)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.PROSECUTE);
                    double percent;
                    switch (level) {
                        case 1,2,3,4 -> percent = level * 0.1;
                        default -> percent = level * 0.3 - 0.8;
                    }
                    event.setDamage(event.getDamage() * (1 + livingEntityStats.getHealth() / livingEntityStats.getMaxHealth() * 100 * (percent/100)));
                }

                if (victim.getHealth() - event.getDamage() <= 0) {
                    if (attackedWeapon.hasEnchantment(Enchantment.EXPERIENCE)) {
                        int level = attackedWeapon.getEnchantmentLevel(Enchantment.EXPERIENCE);
                        NBT.modifyPersistentData(victim, nbt -> {
                            ReadWriteNBT experience = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.EXPERIENCE.name());
                            experience.setDouble(player.getUniqueId().toString(), level * 12.5);
                        });
                    }

                    if (attackedWeapon.hasEnchantment(Enchantment.LOOTING)) {
                        int level = attackedWeapon.getEnchantmentLevel(Enchantment.LOOTING);
                        NBT.modifyPersistentData(victim, nbt -> {
                            ReadWriteNBT looting = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.LOOTING.name());
                            looting.setDouble(player.getUniqueId().toString(), (double) level * 15);
                        });
                    }

                    if (attackedWeapon.hasEnchantment(Enchantment.LUCK)) {
                        int level = attackedWeapon.getEnchantmentLevel(Enchantment.LUCK);
                        NBT.modifyPersistentData(victim, nbt -> {
                            ReadWriteNBT luck = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.LUCK.name());
                            luck.setDouble(player.getUniqueId().toString(), (double) level * 5);
                        });
                    }

                    if (attackedWeapon.hasEnchantment(Enchantment.SCAVENGER)) {
                        int level = attackedWeapon.getEnchantmentLevel(Enchantment.SCAVENGER);
                        NBT.modifyPersistentData(victim, nbt -> {
                            ReadWriteNBT luck = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.SCAVENGER.name());
                            luck.setDouble(player.getUniqueId().toString(), (double) level * 0.3);
                        });
                    }

                    victim.damage(event.getDamage());
                    cancel();
                    return;
                } else {
                    victim.damage(event.getDamage());
                }
                new DamageIndicatorAPI(plugin, event.getDamage(), victim.getLocation(),
                        isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);

                if (attackedWeapon.hasEnchantment(Enchantment.MANA_STEAL)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.MANA_STEAL);
                    double percentDamage = level * 0.0025;
                    attackerStats.setMana(attackerStats.getMana() + (attackerStats.getMana() * percentDamage));
                }

                if (attackedWeapon.hasEnchantment(Enchantment.LIFE_STEAL)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.LIFE_STEAL);
                    double percentDamage = level * 0.005;
                    attackerStats.setHealth(attackerStats.getHealth() + (event.getDamage() * percentDamage));
                }

                if (attackedWeapon.hasEnchantment(Enchantment.FIRE_ASPECT)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
                    int sec;
                    double percentDamage;
                    switch (level) {
                        case 1 -> {
                            percentDamage = 0.03;
                            sec = 3;
                        }
                        case 2 -> {
                            percentDamage = 0.06;
                            sec = 4;
                        }
                        case 3 -> {
                            percentDamage = 0.09;
                            sec = 4;
                        }
                        default -> {
                            percentDamage = level * 0.05;
                            sec = 5;
                        }
                    }
                    victim.setFireTicks(sec * 20);
                    double temp = event.getDamage() * percentDamage;
                    new BukkitRunnable() {
                        private int count = 0;
                        @Override
                        public void run() {
                            if (count >= sec) {
                                cancel();
                                return;
                            }

                            victim.damage(temp);
                            new DamageIndicatorAPI(plugin, temp, victim.getLocation(), DamageIndicatorAPI.DamageCause.FIRE);

                            count++;
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }

                if (attackedWeapon.hasEnchantment(Enchantment.CLEAVE)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.CLEAVE);
                    List<LivingEntity> nearbyLivingEntities;
                    double percentDamage;
                    switch (level) {
                        case 1 -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), 3.3);
                            percentDamage = 0.03;
                        }
                        case 2 -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), 3.6);
                            percentDamage = 0.06;
                        }
                        case 3 -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), 3.9);
                            percentDamage = 0.09;
                        }
                        case 4 -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), 4.2);
                            percentDamage = 0.12;
                        }
                        case 5 -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), 4.5);
                            percentDamage = 0.15;
                        }
                        case 6 -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), 4.8);
                            percentDamage = 0.2;
                        }
                        default -> {
                            nearbyLivingEntities = getLivingEntitiesWithinRadius(victim.getLocation(), level * 0.8);
                            percentDamage = level * 0.05 - 0.1;
                        }
                    }
                    nearbyLivingEntities.forEach(livingEntity -> {
                        if (!(livingEntity instanceof Player)) {
                            double enchantDamage = event.getDamage() * percentDamage;
                            livingEntity.damage(enchantDamage);
                            new DamageIndicatorAPI(plugin, event.getDamage(), livingEntity.getLocation(),
                                    isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);
                        }
                    });
                }

                if (attackedWeapon.hasEnchantment(Enchantment.SYPHON)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.SYPHON);
                    double percent = level * 0.1 + 0.1;
                    int multi = (int) Math.floor(Math.min(attackerStats.getCritDamage(), 1000) / 100);
                    double finalMulti = multi * percent / 100;
                    attackerStats.setHealth(attackerStats.getHealth() + (event.getDamage() * finalMulti));
                }

                attackCount++;
            }
        }.runTaskTimer(this.plugin, 0L, 10L);
    }

    private final Set<Player> meleeCooldownSet = new HashSet<>();
    @EventHandler
    public void onPlayerDamageLivingEntityByMelee(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!(event.getDamager() instanceof Player player)) return;

        if (!meleeCooldownSet.add(player)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                meleeCooldownSet.remove(player);
            }
        }.runTaskLater(this.plugin, 10);
        applyKnockback(victim, player);
        event.setDamage(0);
        PlayerDamageLivingEntityByMeleeEvent meleeEvent = new PlayerDamageLivingEntityByMeleeEvent(player, victim, event.getDamage());
        Bukkit.getPluginManager().callEvent(meleeEvent);
    }

    private final Set<Player> projectileCooldownSet = new HashSet<>();
    @EventHandler
    public void onPlayerDamageLivingEntityByProjectile(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) return;
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Player player)) return;

        if (!projectileCooldownSet.add(player)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                projectileCooldownSet.remove(player);
            }
        }.runTaskLater(this.plugin, 10);
        applyKnockback(victim, player);
        event.setDamage(0);
        PlayerDamageLivingEntityByProjectileEvent projectileEvent = new PlayerDamageLivingEntityByProjectileEvent(player, projectile, victim, event.getDamage());
        Bukkit.getPluginManager().callEvent(projectileEvent);
    }

    private double getAdditiveMultiplier(Player attacker, LivingEntity damagedLivingEntity) {
        double finalMultiplier = 1;

        if (attacker.getEquipment() == null || attacker.getEquipment().getItemInMainHand().getType() == Material.AIR) return finalMultiplier;

        ItemBuilderAPI attackedWeapon = new ItemBuilderAPI(attacker.getEquipment().getItemInMainHand());
        EntityType damagedLivingEntityType = damagedLivingEntity.getType();
        switch (damagedLivingEntity.getType()) {
            case SPIDER, CAVE_SPIDER, SILVERFISH, ENDERMITE, BEE -> {
                if (attackedWeapon.hasEnchantment(Enchantment.BANE_OF_ARTHROPODS)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS);
                    switch (level) {
                        case 1 -> finalMultiplier += ((double) 10/100);
                        case 2 -> finalMultiplier += ((double) 20/100);
                        case 3 -> finalMultiplier += ((double) 30/100);
                        case 4 -> finalMultiplier += ((double) 40/100);
                        case 5 -> finalMultiplier += ((double) 60/100);
                        case 6 -> finalMultiplier += ((double) 80/100);
                        case 7 -> finalMultiplier += ((double) 100/100);
                        default -> finalMultiplier += ((double) (level * 10 + 30)/100);
                    }
                }
            }
            case DROWNED, HUSK, PHANTOM, SKELETON, SKELETON_HORSE, STRAY, WITHER, WITHER_SKELETON, ZOGLIN, ZOMBIE, ZOMBIE_HORSE, ZOMBIE_VILLAGER, ZOMBIFIED_PIGLIN -> {
                if (attackedWeapon.hasEnchantment(Enchantment.SMITE)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.SMITE);
                    switch (level) {
                        case 1 -> finalMultiplier += ((double) 10/100);
                        case 2 -> finalMultiplier += ((double) 20/100);
                        case 3 -> finalMultiplier += ((double) 30/100);
                        case 4 -> finalMultiplier += ((double) 40/100);
                        case 5 -> finalMultiplier += ((double) 60/100);
                        case 6 -> finalMultiplier += ((double) 80/100);
                        case 7 -> finalMultiplier += ((double) 100/100);
                        default -> finalMultiplier += ((double) (level * 10 + 30)/100);
                    }
                }
            }
            case AXOLOTL, DOLPHIN, SQUID, GLOW_SQUID, GUARDIAN, ELDER_GUARDIAN, TURTLE, COD, SALMON, PUFFERFISH, TROPICAL_FISH -> {
                if (attackedWeapon.hasEnchantment(Enchantment.IMPALING)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.IMPALING);
                    finalMultiplier += ((double) (level * 25)/100);
                }
            }
            case CREEPER, MAGMA_CUBE, SLIME -> {
                if (attackedWeapon.hasEnchantment(Enchantment.CUBISM)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.CUBISM);
                    switch (level) {
                        case 1 -> finalMultiplier += ((double) 10/100);
                        case 2 -> finalMultiplier += ((double) 20/100);
                        case 3 -> finalMultiplier += ((double) 30/100);
                        case 4 -> finalMultiplier += ((double) 40/100);
                        case 5 -> finalMultiplier += ((double) 60/100);
                        case 6 -> finalMultiplier += ((double) 80/100);
                        default -> finalMultiplier += ((double) (level * 10 + 30)/100);
                    }
                }
            }
            case ENDER_DRAGON -> {
                if (attackedWeapon.hasEnchantment(Enchantment.DRAGON_HUNTER)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.DRAGON_HUNTER);
                    finalMultiplier += ((double) (level * 8)/100);
                }
            }
            case BLAZE -> {
                if (attackedWeapon.hasEnchantment(Enchantment.SMOLDERING)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.SMOLDERING);
                    finalMultiplier += ((double) (level * 3)/100);
                }
            }
        }
        switch (damagedLivingEntityType) {
            case ENDERMAN, ENDERMITE, ENDER_DRAGON -> {
                if (attackedWeapon.hasEnchantment(Enchantment.ENDER_SLAYER)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.ENDER_SLAYER);
                    switch (level) {
                        case 1 -> finalMultiplier += ((double) 15/100);
                        case 2 -> finalMultiplier += ((double) 30/100);
                        case 3 -> finalMultiplier += ((double) 45/100);
                        case 4 -> finalMultiplier += ((double) 60/100);
                        case 5 -> finalMultiplier += ((double) 80/100);
                        case 6 -> finalMultiplier += ((double) 100/100);
                        case 7 -> finalMultiplier += ((double) 130/100);
                        default -> finalMultiplier += ((double) (level * 20)/100);
                    }
                }
            }
        }

        if (attackedWeapon.hasEnchantment(Enchantment.SHARPNESS)) {
            int level = attackedWeapon.getEnchantmentLevel(Enchantment.SHARPNESS);
            switch (level) {
                case 1 -> finalMultiplier += ((double) 5/100);
                case 2 -> finalMultiplier += ((double) 10/100);
                case 3 -> finalMultiplier += ((double) 15/100);
                case 4 -> finalMultiplier += ((double) 20/100);
                case 5 -> finalMultiplier += ((double) 30/100);
                case 6 -> finalMultiplier += ((double) 45/100);
                case 7 -> finalMultiplier += ((double) 65/100);
                default -> finalMultiplier += ((double) (level * 20 - 80)/100);
            }
        }

        if (attackedWeapon.hasEnchantment(Enchantment.EXECUTE)) {
            int level = attackedWeapon.getEnchantmentLevel(Enchantment.SHARPNESS);
            double temp;
            switch (level) {
                case 1 -> temp = (0.2/100);
                case 2 -> temp = (0.4/100);
                case 3 -> temp = (0.6/100);
                case 4 -> temp = (0.8/100);
                case 5 -> temp = (1.0/100);
                case 6 -> temp = (1.25/100);
                default -> temp = ((level * 0.25 - 0.25)/100);
            }
            finalMultiplier += (temp * new LivingEntityStatistic(damagedLivingEntity).getMissingHealthPercent());
        }

        return finalMultiplier;
    }

    private double getMultiplicativeMultiplier() {
        return 1;
    }

    private double getBonusModifiers() {
        return 0;
    }

    public List<LivingEntity> getLivingEntitiesWithinRadius(Location location, double radius) {
        // Fetch all nearby entities
        return Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, radius, radius, radius).stream()
                .filter(entity -> entity instanceof LivingEntity) // Filter for LivingEntity instances
                .map(entity -> (LivingEntity) entity) // Cast to LivingEntity
                .collect(Collectors.toList());
    }

    private final Random random = new Random();
    private void applyKnockback(LivingEntity livingEntity, Entity attacker){
        random.nextDouble();
        double dist = (attacker instanceof Player && ((Player) attacker).isSprinting()) ? 1.5 : 1;
        dist += random.nextDouble()*0.4-0.2; // adds or subtract 0.2 to the distance
        int knockBackLevel = getKnockBackLevel(attacker);
        dist += 3*knockBackLevel;
        if(reducedKnockback.contains(livingEntity.getType())){
            dist *= 0.5;
        }
        double mag = distanceToMagnitude(dist);
        Location location = getLocation(attacker);
        location.setPitch(location.getPitch()-15);
        Vector velocity = setMag(location.getDirection(),mag);
        livingEntity.setVelocity(velocity);
    }

    private Location getLocation(Entity entity){
        if(entity instanceof Projectile projectile){
            Location location = entity.getLocation();
            location.setDirection(projectile.getVelocity());
            return location;
        }else{
            return entity.getLocation();
        }
    }

    private double distanceToMagnitude(double distance){
        return ((distance + 1.5)/5d);
    }

    private Vector setMag(Vector vector, double mag){
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();
        double denominator = Math.sqrt(x*x + y*y + z*z);
        if(denominator != 0 ){
            return vector.multiply(mag/denominator);
        }else{
            return vector;
        }
    }

    private int getKnockBackLevel(Entity entity){
        if (entity instanceof LivingEntity){
            ItemStack mainHand = Objects.requireNonNull(((LivingEntity) entity).getEquipment()).getItemInMainHand();
            ItemBuilderAPI itemBuilderAPI = new ItemBuilderAPI(mainHand);
            if (itemBuilderAPI.hasEnchantment(Enchantment.KNOCKBACK)) {
                return itemBuilderAPI.getEnchantmentLevel(Enchantment.KNOCKBACK);
            }
        } else {
            return 0;
        }
        return 0;
    }

    private final ImmutableSet<EntityType> reducedKnockback = new ImmutableSet.Builder<EntityType>()
            .add(EntityType.VEX)
            .build();
}
