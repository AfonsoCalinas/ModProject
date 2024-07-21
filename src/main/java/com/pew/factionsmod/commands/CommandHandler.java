package com.pew.factionsmod.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.pew.factionsmod.factions.Faction;
import com.pew.factionsmod.factions.FactionManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityArgument;

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

                                            // Check if the player is already in a faction
                                            Faction existingFaction = factionManager.getFactionByPlayer(playerId);
                                            if (existingFaction != null) {
                                                source.sendFailure(Component.literal("You are already in a faction!"));
                                                return 0; // Command failed
                                            }

                                            // Create a new faction
                                            factionManager.createFaction(name, playerId);
                                            source.sendSuccess(Component.literal("Faction created!"), true);
                                            return 1; // Command Succeeded
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
                                    return 1; // Command Succeeded
                                }))
                        .then(Commands.literal("list")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    if (factionManager.getFactions().isEmpty()) {
                                        source.sendSuccess(Component.literal("There are no factions."), true);
                                    } else {
                                        source.sendSuccess(Component.literal("Factions:"), true);
                                        for (Faction faction : factionManager.getFactions().values()) {
                                            source.sendSuccess(Component.literal("- " + faction.getName()), true);
                                        }
                                    }
                                    return 1; // Command Succeeded
                                }))
                        .then(Commands.literal("leave")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    UUID playerId = source.getPlayerOrException().getUUID();
                                    Faction faction = factionManager.getFactionByPlayer(playerId);

                                    if (faction != null) {
                                        // Handle leaving the faction
                                        factionManager.leaveFaction(playerId);

                                        source.sendSuccess(Component.literal("You have left the faction."), true);
                                    } else {

                                        source.sendSuccess(Component.literal("You are not in a faction!"), false);

                                    }

                                    return 1;
                                }))
                        .then(Commands.literal("promote")
                                .then(Commands.argument("name", EntityArgument.player())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "name");
                                            UUID leaderPlayerId = source.getPlayerOrException().getUUID();
                                            UUID targetPlayerId = targetPlayer.getUUID();
                                            Faction leaderFaction = factionManager.getFactionByPlayer(leaderPlayerId);
                                            Faction targetPlayerFaction = factionManager.getFactionByPlayer(targetPlayerId);

                                            if (leaderFaction == null) {
                                                source.sendFailure(Component.literal("You are not in a faction!"));
                                                return 0; // Command failed
                                            }

                                            if (!leaderFaction.isLeader(leaderPlayerId)) {
                                                source.sendFailure(Component.literal("You are not the faction's leader!"));
                                                return 0; // Command failed
                                            }

                                            if (leaderPlayerId == targetPlayerId) {
                                                source.sendFailure(Component.literal("You can't promote yourself!"));
                                                return 0; // Command failed
                                            }

                                            if (!(leaderFaction == targetPlayerFaction)) {
                                                source.sendFailure(Component.literal("That player is not in your faction!"));
                                                return 0; // Command failed
                                            }

                                            leaderFaction.setLeader(targetPlayerId);

                                            source.sendSuccess(Component.literal(targetPlayer.getName().getString() + " promoted to leader!"), true);

                                            targetPlayer.sendSystemMessage(Component.literal("You have been promoted to leader of the faction!"), false);

                                            return 1; // Command succeeded
                                        })))
                        .then(Commands.literal("invite")
                                .then(Commands.argument("name", EntityArgument.player())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            ServerPlayer invitee = EntityArgument.getPlayer(context, "name");
                                            UUID inviterId = source.getPlayerOrException().getUUID();
                                            UUID inviteeId = invitee.getUUID();

                                            // Check if inviter is in a faction
                                            if (factionManager.getFactionByPlayer(inviterId) == null) {
                                                source.sendFailure(Component.literal("You're not in a faction!"));
                                                return 0; // Command failed
                                            }

                                            // Find the invitee to invite
                                            /*if (invitee == null) {
                                                source.sendFailure(Component.literal("Player not found!"));
                                                return 0; // Command failed
                                            }*/

                                            // Check if invitee is already in a faction
                                            if (factionManager.getFactionByPlayer(inviteeId) != null) {
                                                source.sendFailure(Component.literal(invitee.getName().getString() + " is already in a faction!"));
                                                return 0; // Command failed
                                            }

                                            // Invite the player
                                            if (factionManager.invitePlayer(inviterId, inviteeId)) {
                                                source.sendSuccess(Component.literal("Player invited!"), true);
                                                invitee.sendSystemMessage(Component.literal("You have been invited to join a faction! Use /faction accept to join."));
                                                return 1; // Command succeeded
                                            } else {
                                                source.sendFailure(Component.literal("You are not the leader of a faction or already invited this player!"));
                                                return 0; // Command failed
                                            }
                                        })))
                        .then(Commands.literal("accept")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    UUID playerId = source.getPlayerOrException().getUUID();

                                    if (factionManager.acceptInvitation(playerId)) {
                                        source.sendSuccess(Component.literal("You have joined the faction!"), true);
                                        return 1; // Command succeeded
                                    } else {
                                        source.sendFailure(Component.literal("You have no pending invitations!"));
                                        return 0; // Command failed
                                    }
                                }))
                // Add more commands for joining, leaving, etc.
        );
    }
}
