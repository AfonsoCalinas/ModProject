package com.pew.factionsmod.data;

import com.pew.factionsmod.factions.FactionManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class FactionData extends SavedData {
    private final FactionManager factionManager;

    public static final String DATA_NAME = "faction_data";

    public FactionData(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    public static FactionData load(CompoundTag nbt) {
        // Implement your loading logic here.
        FactionManager factionManager = new FactionManager();
        // ... load the factions into the manager
        return new FactionData(factionManager);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        // Implement your saving logic here.
        return compound;
    }

    public static String getDataName() {
        return DATA_NAME;
    }
}
