package com.pew.factionsmod.factions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Faction {
    private final String name;
    private UUID leader;
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

    public void setLeader(UUID newLeader) {
        if (members.contains(newLeader)) {
            this.leader = newLeader;
        }
    }

    public UUID getLeader() {
        return leader;
    }

    public boolean isLeader(UUID player) {
        return leader.equals(player);
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