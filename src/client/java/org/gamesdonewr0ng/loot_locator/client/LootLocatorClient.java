package org.gamesdonewr0ng.loot_locator.client;

import com.seedfinding.mccore.version.MCVersion;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import org.gamesdonewr0ng.loot_locator.client.commands.FinderCommand;
import org.gamesdonewr0ng.loot_locator.client.util.LootTableHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public enum LootLocatorClient {
    INSTANCE;

    public Logger LOGGER;
    public MinecraftClient MC;
    public LootTableHelper lootTableHelper;
    public MCVersion VERSION;

    public long seed;

    public void initialize() {
        LOGGER = LoggerFactory.getLogger("Loot Locator");
        MC = MinecraftClient.getInstance();
        VERSION = MCVersion.v1_21;

        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            FinderCommand.register(dispatcher, registryAccess);
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                lootTableHelper = new LootTableHelper(Objects.requireNonNull(MC.getServer()).getReloadableRegistries()));
    }
}
