package com.pew.factionsmod.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.pew.factionsmod.factions.Faction;
import com.pew.factionsmod.factions.FactionManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class CommandHandler {
    private final FactionManager factionManager;

    public CommandHandler(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("faction")
                        .then(Commands.literal("create")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            CommandSourceStack source = context.getSource();
                                            UUID playerId = source.getPlayerOrException().getUUID();
                                            factionManager.createFaction(name, playerId);
                                            source.sendSuccess(Component.literal("Faction created!"), true);
                                            return 1;
                                        })))
                        .then(Commands.literal("disband")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    UUID playerId = source.getPlayerOrException().getUUID();
                                    Faction faction = factionManager.getFactionByPlayer(playerId);
                                    if (faction != null && faction.getLeader().equals(playerId)) {
                                        factionManager.disbandFaction(faction.getName());
                                        source.sendSuccess(Component.literal("Faction disbanded!"), true);
                                    } else {
                                        source.sendFailure(Component.literal("You are not the leader of a faction!"));
                                    }
                                    return 1;
                                }))
                // Add more commands for joining, leaving, etc.
        );
    }
}
