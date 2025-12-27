package com.hugo;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerSuggestionProvider implements com.mojang.brigadier.suggestion.SuggestionProvider<net.minecraft.server.command.ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String input = builder.getRemaining().toLowerCase();
        List<UUID> playerTPAList = TPAUtilities.listPlayerTPA(Objects.requireNonNull(context.getSource().getPlayer()).getUuid());
        List<UUID> playerTPAHEREList = TPAUtilities.listPlayerTPAHERE(Objects.requireNonNull(context.getSource().getPlayer()).getUuid());
        List<UUID> alreadyAdded = new ArrayList<>();

        if (playerTPAList != null){
            for (UUID player_uuid : playerTPAList){
                String player_name = Objects.requireNonNull(context.getSource().getServer().getPlayerManager().getPlayer(player_uuid)).getName().getString();
                if (player_name.toLowerCase().startsWith(input) && !alreadyAdded.contains(player_uuid)) {
                    alreadyAdded.add(player_uuid);
                    builder.suggest(player_name);
                }
            }
        }

        if (playerTPAHEREList != null){
            for (UUID player_uuid : playerTPAHEREList){
                String player_name = Objects.requireNonNull(context.getSource().getServer().getPlayerManager().getPlayer(player_uuid)).getName().getString();
                if (player_name.toLowerCase().startsWith(input) && !alreadyAdded.contains(player_uuid)) {
                    alreadyAdded.add(player_uuid);
                    builder.suggest(player_name);
                }
            }
        }

        return builder.buildFuture();

    }
}