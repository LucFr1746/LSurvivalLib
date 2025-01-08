package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import io.github.lucfr1746.LSurvivalLib.Enchantments.Enchantment;
import io.github.lucfr1746.LSurvivalLib.Entity.LivingEntity.LivingEntityStatistic;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.PlayerDamageLivingEntityByMeleeEvent;
import io.github.lucfr1746.LSurvivalLib.Entity.Player.PlayerStatistic;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlayerDamageLivingEntityListener implements Listener {

    private final LSurvivalLib plugin;

    private final List<EntityType> undead = Arrays.asList(EntityType.DROWNED, EntityType.HUSK, EntityType.PHANTOM, EntityType.SKELETON, EntityType.SKELETON_HORSE,
                                            EntityType.STRAY, EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIE,
                                            EntityType.ZOMBIE_HORSE, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIFIED_PIGLIN);
    private final List<EntityType> aquatic = Arrays.asList(EntityType.AXOLOTL, EntityType.DOLPHIN, EntityType.SQUID, EntityType.GLOW_SQUID, EntityType.GUARDIAN,
                                             EntityType.ELDER_GUARDIAN, EntityType.TURTLE, EntityType.COD, EntityType.SALMON, EntityType.PUFFERFISH,
                                             EntityType.TROPICAL_FISH);
    private final List<EntityType> nether = List.of(EntityType.BLAZE, EntityType.GHAST, EntityType.HOGLIN, EntityType.MAGMA_CUBE, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE,
                                                    EntityType.WITHER_SKELETON, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN);
    private final List<EntityType> arthropod = Arrays.asList(EntityType.BEE, EntityType.CAVE_SPIDER, EntityType.ENDERMITE, EntityType.SILVERFISH, EntityType.SPIDER);
    private final List<EntityType> illager = Arrays.asList(EntityType.PILLAGER, EntityType.ILLUSIONER, EntityType.RAVAGER, EntityType.EVOKER, EntityType.VINDICATOR);
    private final List<EntityType> end = Arrays.asList(EntityType.ENDERMITE, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.SHULKER);
    private final List<EntityType> cubism = Arrays.asList(EntityType.CREEPER, EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.GHAST, EntityType.STRIDER);
    private final List<EntityType> ender_dragon = List.of(EntityType.ENDER_DRAGON);

    public PlayerDamageLivingEntityListener(LSurvivalLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamageLivingEntityByMelee(PlayerDamageLivingEntityByMeleeEvent event) {
        Player player = event.getPlayer();
        LivingEntity victim = event.getVictim();
        LivingEntityStatistic victimStat = new LivingEntityStatistic(victim);

        PlayerStatistic attackerStat = new PlayerStatistic(player);
        ItemBuilderAPI attackedWeapon = new ItemBuilderAPI(player.getInventory().getItemInMainHand());

        boolean correctWeapon = attackedWeapon.getType() != Material.AIR && Set.of(Category.SWORD, Category.LONG_SWORD, Category.FISHING_WEAPON, Category.PICKAXE, Category.AXE, Category.MACE)
                .contains(attackedWeapon.getCategory());

//        if (!correctWeapon) {
//            event.setDamage(Statistic.OtherStats.DAMAGE.getBaseValue());
//            victim.damage(event.getDamage());
//            new DamageIndicatorAPI(plugin, event.getDamage(), victim.getLocation(),  DamageIndicatorAPI.DamageCause.NORMAL);
//            return;
//        }

        double ferocity = attackerStat.getFerocity();
        int extraAttacks = (int) Math.floor(ferocity / 100);
        if (ThreadLocalRandom.current().nextDouble() <= (ferocity % 100) / 100) extraAttacks++;

        final float attackCooldown = player.getAttackCooldown();
        int finalExtraAttacks = extraAttacks;
        new BukkitRunnable() {
            private int attackCount = 0;

            @Override
            public void run() {
                if (attackCount > finalExtraAttacks) {
                    cancel();
                    return;
                }

                double damage = attackerStat.getDamage()
                        * (1 + attackerStat.getStrength() / 100)
                        * getAdditiveMultiplier(player, victim)
                        * getMultiplicativeMultiplier()
                        + getBonusModifiers();

                boolean isCrit = ThreadLocalRandom.current().nextDouble() <= (attackerStat.getCritChance() / 100);
                if (isCrit) {
                    damage *= (1 + attackerStat.getCritDamage() / 100);
                    if (attackedWeapon.hasEnchantment(Enchantment.CRITICAL)) {
                        int level = attackedWeapon.getEnchantmentLevel(Enchantment.CRITICAL);
                        double multi = (level <= 5) ? level * 10 : (level <= 6) ? 70 : level * 30 - 110;
                        damage *= multi / 100;
                    }
                }

                damage *= attackCooldown;
                damage *= (1 - victimStat.getDefense() / (victimStat.getDefense() + 100));
                event.setDamage(damage);

                if (victim.getHealth() - damage <= 0) {
                    handleEntityDeath(player, victim, attackedWeapon);
                    victim.setSilent(true);
                    victim.damage(damage);
                    if (victim.getDeathSound() != null) player.playSound(victim.getLocation(), victim.getDeathSound(), 1, 1);
                    new DamageIndicatorAPI(plugin, damage, victim.getLocation(),
                            isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);
                    cancel();
                    return;
                } else {
                    victim.damage(damage);
                }

                new DamageIndicatorAPI(plugin, damage, victim.getLocation(),
                        isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);

                // Handle enchant effects
                applyEnchantmentEffect(attackedWeapon, player, victim);

                // Handle Life steal
                if (attackedWeapon.hasEnchantment(Enchantment.LIFE_STEAL)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.LIFE_STEAL);
                    double multi = level * 0.5;
                    attackerStat.setHealth(attackerStat.getHealth() + (event.getDamage() * multi / 100));
                }

                // Handle Syphon
                if (attackedWeapon.hasEnchantment(Enchantment.SYPHON)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.SYPHON);
                    double multi = level * 0.1 + 0.1;
                    int time = (int) Math.floor(Math.min(attackerStat.getCritDamage(), 1000) / 100);
                    attackerStat.setHealth(attackerStat.getHealth() + (event.getDamage() * multi * time / 100));
                }

                // Handle Fire aspect
                if (attackedWeapon.hasEnchantment(Enchantment.FIRE_ASPECT)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
                    int sec = (level <= 1) ? 3 : (level <= 3) ? 4 : 5;
                    double multi = level * 3;
                    victim.setFireTicks(sec * 20);
                    double enchantDamage = event.getDamage() * multi / 100;
                    new BukkitRunnable() {
                        private int count = 0;
                        @Override
                        public void run() {
                            if (count >= sec) {
                                cancel();
                                return;
                            }

                            victim.damage(enchantDamage);
                            new DamageIndicatorAPI(plugin, enchantDamage, victim.getLocation(), DamageIndicatorAPI.DamageCause.FIRE);

                            count++;
                        }
                    }.runTaskTimer(plugin, 0L, 20L);
                }

                // Handle Cleave
                if (attackedWeapon.hasEnchantment(Enchantment.CLEAVE)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.CLEAVE);
                    double radius = level * 0.3 + 3;
                    double multi = (level <= 5) ? level * 3 : level * 6 * 5 - 10;
                    getLivingEntitiesWithinRadius(victim.getLocation(), radius).stream()
                            .filter(livingEntity -> (!(livingEntity instanceof Player) && livingEntity != victim)).forEach(livingEntity -> {
                                double enchantDamage = event.getDamage() * multi / 100;
                                livingEntity.damage(enchantDamage);
                                new DamageIndicatorAPI(plugin, event.getDamage(), livingEntity.getLocation(),
                                        isCrit ? DamageIndicatorAPI.DamageCause.CRITICAL : DamageIndicatorAPI.DamageCause.NORMAL);
                    });
                }

                // Handle Thunderbolt
                if (attackedWeapon.hasEnchantment(Enchantment.THUNDERBOLT)) {
                    int level = attackedWeapon.getEnchantmentLevel(Enchantment.THUNDERBOLT);
                    double multi = (level <= 5) ? level * 4 : level * 5 - 5;
                    int stackTime = NBT.modifyPersistentData(victim, nbt -> {
                        ReadWriteNBT thunderbolt = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.THUNDERBOLT.name());
                        if (!thunderbolt.hasTag(player.getUniqueId().toString())) {
                            thunderbolt.setInteger(player.getUniqueId().toString(), 0);
                        }
                        thunderbolt.setInteger(player.getUniqueId().toString(), thunderbolt.getInteger(player.getUniqueId().toString()) + 1);
                        return thunderbolt.getInteger(player.getUniqueId().toString());
                    });
                    if (stackTime % 3 == 0) {
                        getLivingEntitiesWithinRadius(victim.getLocation(), 2).stream()
                                .filter(livingEntity -> (!(livingEntity instanceof Player) && livingEntity != victim)).forEach(livingEntity -> {
                                    double enchantDamage = event.getDamage() * multi / 100;
                                    livingEntity.damage(enchantDamage);
                                    new DamageIndicatorAPI(plugin, event.getDamage(), livingEntity.getLocation(), DamageIndicatorAPI.DamageCause.LIGHTNING);
                                    livingEntity.getWorld().strikeLightningEffect(livingEntity.getLocation());
                        });
                    }
                }

                attackCount++;
            }
        }.runTaskTimer(this.plugin, 0L, 10L);
    }

    private double getAdditiveMultiplier(Player attacker, LivingEntity victim) {
        double finalMultiplier = 1;

        if (attacker.getEquipment() == null || attacker.getEquipment().getItemInMainHand().getType() == Material.AIR)
            return finalMultiplier;

        ItemBuilderAPI attackedWeapon = new ItemBuilderAPI(attacker.getEquipment().getItemInMainHand());

        finalMultiplier += applyEnchantmentBonus(attackedWeapon, attacker, victim);

        return finalMultiplier;
    }

    private void applyEnchantmentEffect(ItemBuilderAPI weapon, Player attacker, LivingEntity victim) {
        PlayerStatistic attackerStat = new PlayerStatistic(attacker);
        LivingEntityStatistic victimStat = new LivingEntityStatistic(victim);

        // Handle Lethality
        if (weapon.hasEnchantment(Enchantment.LETHALITY)) {
            int level = weapon.getEnchantmentLevel(Enchantment.LETHALITY);
            double multi = (level <= 5) ? level * 1.2 : level * 3 - 9;
            int stackTime = NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT lethality = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.LETHALITY.name());
                if (!lethality.hasTag(attacker.getUniqueId().toString())) {
                    lethality.setInteger(attacker.getUniqueId().toString(), 0);
                }
                lethality.setInteger(attacker.getUniqueId().toString(), lethality.getInteger(attacker.getUniqueId().toString()) + 1);
                return lethality.getInteger(attacker.getUniqueId().toString());
            });
            if (stackTime <= 4) victimStat.setDefense(victimStat.getDefense() - victimStat.getDefense() * multi / 100);
        }

        // Handle Mana steal
        if (weapon.hasEnchantment(Enchantment.MANA_STEAL)) {
            int level = weapon.getEnchantmentLevel(Enchantment.MANA_STEAL);
            double multi = level * 0.25;
            attackerStat.setMana(attackerStat.getMana() + (attackerStat.getMana() * multi / 100));
        }
    }

    private double applyEnchantmentBonus(ItemBuilderAPI weapon, Player attacker, LivingEntity victim) {
        PlayerStatistic attackerStat = new PlayerStatistic(attacker);
        LivingEntityStatistic victimStat = new LivingEntityStatistic(victim);
        double multiplier = 0;

        // Handle the Bane of arthropods, smite, cubism, impaling, smoldering, ender slayer, dragon hunter
        Map<List<EntityType>, Enchantment> enchantmentMap = Map.of(
                arthropod, Enchantment.BANE_OF_ARTHROPODS,
                undead, Enchantment.SMITE,
                cubism, Enchantment.CUBISM,
                aquatic, Enchantment.IMPALING,
                nether, Enchantment.SMOLDERING,
                end, Enchantment.ENDER_SLAYER,
                ender_dragon, Enchantment.DRAGON_HUNTER
                // illager, Enchantment.ELIMINATE_EVIL
        );
        for (Map.Entry<List<EntityType>, Enchantment> entry : enchantmentMap.entrySet()) {
            if (entry.getKey().contains(victim.getType())) {
                Enchantment enchant = entry.getValue();
                if (weapon.hasEnchantment(enchant)) {
                    int level = weapon.getEnchantmentLevel(enchant);
                    double multi = switch (enchant) {
                        case BANE_OF_ARTHROPODS, SMITE, CUBISM -> (level <= 4) ? level * 10 : level * 20 - 40;
                        case IMPALING -> level * 25;
                        case SMOLDERING -> level * 3;
                        case DRAGON_HUNTER -> level * 8;
                        case ENDER_SLAYER -> (level <= 4) ? level * 15 : (level <= 6) ? level * 20 - 20 : level * 30 - 80;
                        default -> 0;
                    };
                    multiplier += multi / 100;
                }
            }
        }

        // Handle Sharpness
        if (weapon.hasEnchantment(Enchantment.SHARPNESS)) {
            int level = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);
            double multi = (level <= 4) ? level * 5 : (level <= 6) ? level * 15 - 45 : level * 20 - 75;
            multiplier += multi / 100;
        }

        // Handle Execute
        if (weapon.hasEnchantment(Enchantment.EXECUTE)) {
            int level = weapon.getEnchantmentLevel(Enchantment.EXECUTE);
            double multi = (level <= 5) ? level * 0.2 : level * 0.25 - 0.25;
            multiplier += multi * new LivingEntityStatistic(victim).getMissingHealthPercent() / 100;
        }

        // Handle First strike
        if (weapon.hasEnchantment(Enchantment.FIRST_STRIKE)) {
            int level = weapon.getEnchantmentLevel(Enchantment.FIRST_STRIKE);
            double multi = level * 25;
            boolean isFirstHit = NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT first_strike = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.FIRST_STRIKE.name());
                if (!first_strike.hasTag(attacker.getUniqueId().toString())) {
                    first_strike.setInteger(attacker.getUniqueId().toString(), 1);
                    return true;
                } else return false;
            });
            if (isFirstHit) multiplier += multi / 100;
        }

        // Handle Triple strike
        if (weapon.hasEnchantment(Enchantment.TRIPLE_STRIKE)) {
            int level = weapon.getEnchantmentLevel(Enchantment.TRIPLE_STRIKE);
            double multi = level * 10;

            boolean isFirstThreeHit = NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT triple_strike = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.TRIPLE_STRIKE.name());
                return triple_strike.getOrDefault(attacker.getUniqueId().toString(), 0) + 1 >= 3;
            });
            if (isFirstThreeHit) multiplier += multi / 100;
        }

        // Handle Prosecute
        if (weapon.hasEnchantment(Enchantment.PROSECUTE)) {
            int level = weapon.getEnchantmentLevel(Enchantment.PROSECUTE);
            double multi = (level <= 4) ? level * 0.1 : level * 0.3 - 0.8;
            double healthInPercent = victimStat.getHealth() / victimStat.getMaxHealth() * 100;
            multiplier += healthInPercent * multi / 100;
        }

        // Handle Giant killer
        if (weapon.hasEnchantment(Enchantment.GIANT_KILLER)) {
            int level = weapon.getEnchantmentLevel(Enchantment.GIANT_KILLER);
            double multi = (level <= 4) ? level + 0.1 : level * 0.3 - 0.9;
            double upto = (level <= 4) ? level * 5 : (level <= 6) ? level * 15 - 45 : level * 20 - 75;
            multiplier += Math.min((victimStat.getHealth() - attackerStat.getHealth()) / attackerStat.getHealth() * 100 * multi, upto) / 100;
        }

        // Handle Titan killer
        if (weapon.hasEnchantment(Enchantment.TITAN_KILLER)) {
            int level = weapon.getEnchantmentLevel(Enchantment.TITAN_KILLER);
            double multi = (level <= 4) ? level * 2 : level * 4 - 8;
            double upto = (level <= 4) ? level * 6 : level * 20 - 60;
            multiplier += Math.min((victimStat.getDefense() / 100 * multi), upto) / 100;
        }

        // Handle Venomous
        if (weapon.hasEnchantment(Enchantment.VENOMOUS)) {
            int level = weapon.getEnchantmentLevel(Enchantment.VENOMOUS);
            double multi = level * 0.3;
            NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT venomous = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment");
                venomous.setInteger(Enchantment.VENOMOUS.name() + "_EFFECT", level);
            });
            int stackTime = NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT venomous = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.VENOMOUS.name());
                return venomous.getOrDefault(attacker.getUniqueId().toString(), 0) + 1;
            });
            multiplier += Math.min(stackTime, 40) * multi / 100;
        }

        // Handle Thunderlord
        if (weapon.hasEnchantment(Enchantment.THUNDERLORD)) {
            int level = weapon.getEnchantmentLevel(Enchantment.THUNDERLORD);
            double multi = (level <= 5) ? level * 4 : level * 10 - 10;
            int stackTime = NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT thunderlord = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.THUNDERLORD.name());
                if (!thunderlord.hasTag(attacker.getUniqueId().toString())) {
                    thunderlord.setInteger(attacker.getUniqueId().toString(), 0);
                }
                thunderlord.setInteger(attacker.getUniqueId().toString(), thunderlord.getInteger(attacker.getUniqueId().toString()) + 1);
                return thunderlord.getInteger(attacker.getUniqueId().toString());
            });
            if (stackTime % 3 == 0) {
                multiplier += multi / 100;
                victim.getWorld().strikeLightningEffect(victim.getLocation());
            }
        }

        return multiplier;
    }

    private double getMultiplicativeMultiplier() {
        return 1;
    }

    private double getBonusModifiers() {
        return 0;
    }

    private void handleEntityDeath(Player player, LivingEntity victim, ItemBuilderAPI weapon) {
        PlayerStatistic attackerStat = new PlayerStatistic(player);
        LivingEntityStatistic victimStat = new LivingEntityStatistic(victim);

        if (weapon.hasEnchantment(Enchantment.EXPERIENCE)) {
            int level = weapon.getEnchantmentLevel(Enchantment.EXPERIENCE);
            NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT experience = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.EXPERIENCE.name());
                experience.setDouble(player.getUniqueId().toString(), level * 12.5);
            });
        }

        if (weapon.hasEnchantment(Enchantment.LOOTING)) {
            int level = weapon.getEnchantmentLevel(Enchantment.LOOTING);
            NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT looting = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.LOOTING.name());
                looting.setDouble(player.getUniqueId().toString(), (double) level * 15);
            });
        }

        if (weapon.hasEnchantment(Enchantment.LUCK)) {
            int level = weapon.getEnchantmentLevel(Enchantment.LUCK);
            NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT luck = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.LUCK.name());
                luck.setDouble(player.getUniqueId().toString(), (double) level * 5);
            });
        }

        if (weapon.hasEnchantment(Enchantment.SCAVENGER)) {
            int level = weapon.getEnchantmentLevel(Enchantment.SCAVENGER);
            NBT.modifyPersistentData(victim, nbt -> {
                ReadWriteNBT luck = nbt.getOrCreateCompound("ExtraAttributes").getOrCreateCompound("enchantment").getOrCreateCompound(Enchantment.SCAVENGER.name());
                luck.setDouble(player.getUniqueId().toString(), (double) level * 0.3);
            });
        }

        if (weapon.hasEnchantment(Enchantment.VAMPIRISM)) {
            int level = weapon.getEnchantmentLevel(Enchantment.VAMPIRISM);
            attackerStat.setHealth(attackerStat.getHealth() + attackerStat.getMaxHealth() * (double) level / 100);
        }
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
}
