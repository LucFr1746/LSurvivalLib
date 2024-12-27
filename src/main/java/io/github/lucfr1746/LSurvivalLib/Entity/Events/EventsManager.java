package io.github.lucfr1746.LSurvivalLib.Entity.Events;

import io.github.lucfr1746.LSurvivalLib.Entity.Events.ArmorChangeEvent.Listener.ArmorListener;
import io.github.lucfr1746.LSurvivalLib.LSurvivalLib;

public class EventsManager {

    private final LSurvivalLib plugin;

    public EventsManager(LSurvivalLib plugin) {
        this.plugin = plugin;
        new ArmorListener(plugin);
    }
}
