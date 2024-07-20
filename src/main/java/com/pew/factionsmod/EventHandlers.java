package com.pew.factionsmod;

import com.pew.factionsmod.data.FactionData;
import com.pew.factionsmod.factions.FactionManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FactionsMod.MODID)
public class EventHandlers {
    private static final FactionManager factionManager = new FactionManager();

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            FactionData factionData = serverLevel.getDataStorage().computeIfAbsent(
                    tag -> FactionData.load((CompoundTag) tag),
                    () -> new FactionData(factionManager),
                    FactionData.getDataName()
            );
        }
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            FactionData factionData = new FactionData(factionManager);
            serverLevel.getDataStorage().set(FactionData.getDataName(), factionData);
        }
    }
}
