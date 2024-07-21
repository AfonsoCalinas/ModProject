package com.pew.factionsmod.factions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;


public class FactionManager {
    private final Map<String, Faction> factions = new HashMap<>();
    private final Map<UUID, String> invitations = new HashMap<>();

    public Faction createFaction(String name, UUID leader) {
        if (factions.containsKey(name)) {
            return null; // Faction with this name already exists
        }
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

    // Leave current faction
    public void leaveFaction(UUID playerId) {
        Faction faction = getFactionByPlayer(playerId);
        if (faction != null) {
            if (faction.getLeader().equals(playerId)) {
                // Leader is leaving, promote a new leader or disband the faction
                List<UUID> members = new ArrayList<>(faction.getMembers());
                members.remove(playerId);
                if (members.isEmpty()) {
                    disbandFaction(faction.getName());
                } else {
                    UUID newLeader = getMostTenuredMember(members);
                    faction.setLeader(newLeader);
                    faction.removeMember(playerId);
                }
            } else {
                faction.removeMember(playerId);
            }
        }
    }

    private UUID getMostTenuredMember(List<UUID> members) {
        // Implement logic to get the member who has been in the faction the longest
        // For simplicity, we'll just return a random member here
        return members.get(new Random().nextInt(members.size()));
    }

    public boolean invitePlayer(UUID inviter, UUID invitee) {
        Faction faction = getFactionByPlayer(inviter);
        if (faction != null && faction.isLeader(inviter)) {
            invitations.put(invitee, faction.getName());
            return true;
        }
        return false;
    }

    public boolean acceptInvitation(UUID player) {
        String factionName = invitations.remove(player);
        if (factionName != null) {
            Faction faction = getFaction(factionName);
            if (faction != null) {
                faction.addMember(player);
                return true;
            }
        }
        return false;
    }

    public boolean isInvited(UUID player) {
        return invitations.containsKey(player);
    }

}
