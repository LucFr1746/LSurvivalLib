package io.github.lucfr1746.LSurvivalLib.Entity.Player.Events.PlayerDamageEvent.Listener;

import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;

public class DamagesRegister {

    public DamagesRegister(LSurvivalLib plugin) {
        new PlayerDeathListener(plugin);
        new FireDamageListener(plugin);
        new PlayerFallListener(plugin);
        new PlayerDrowningListener(plugin);
        new PlayerContactListener(plugin);
        new PlayerExplosionListener(plugin);
        new PlayerMeleeListener(plugin);
        new PlayerProjectileListener(plugin);
        new PlayerDamageLivingEntityListener(plugin);
    }
}
