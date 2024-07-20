package com.pew.factionsmod.factions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Faction {
    private final String name;
    private final UUID leader;
    private final Set<UUID> members;

    public Faction(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
    }

    public String getName() {
        return name;
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }
}