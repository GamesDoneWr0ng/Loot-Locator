package org.gamesdonewr0ng.loot_locator.client.commands;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.util.Identifier;

public class FinderArguments {
    private static final String[] structures = {"Desert_Temple"};

    public static final SuggestionProvider<FabricClientCommandSource> STRUCTURES = SuggestionProviders.register(
            Identifier.of("finder", "structure_provider"),
            (context, builder) -> CommandSource.suggestMatching(structures, builder)
    );
}
