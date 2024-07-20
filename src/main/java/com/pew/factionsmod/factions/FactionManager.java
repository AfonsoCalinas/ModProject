package com.pew.factionsmod.factions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionManager {
    private final Map<String, Faction> factions = new HashMap<>();

    public Faction createFaction(String name, UUID leader) {
        Faction faction = new Faction(name, leader);
        factions.put(name, faction);
        return faction;
    }

    public Faction getFaction(String name) {
        return factions.get(name);
    }

    public Faction getFactionByPlayer(UUID player) {
        return factions.values().stream()
                .filter(f -> f.getMembers().contains(player))
                .findFirst()
                .orElse(null);
    }

    public void disbandFaction(String name) {
        factions.remove(name);
    }

    // Add this method to get the factions
    public Map<String, Faction> getFactions() {
        return factions;
    }
}
