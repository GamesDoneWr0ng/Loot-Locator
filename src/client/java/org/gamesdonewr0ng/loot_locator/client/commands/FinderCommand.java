package org.gamesdonewr0ng.loot_locator.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import org.gamesdonewr0ng.loot_locator.client.Loot_locatorClient;
import org.gamesdonewr0ng.loot_locator.client.util.IItemEntry;
import org.gamesdonewr0ng.loot_locator.client.util.ILootTable;

import java.util.stream.Stream;

public class FinderCommand {
    static String helpMessage = """
Usage options:
/finder loot <item>""";
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("finder")
                .then(ClientCommandManager.literal("loot")
                        .then(ClientCommandManager.argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                .executes(context -> {
                                    Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                    Stream<RegistryKey<LootTable>> lootTables = LootTables.getAll().stream().filter(i -> isInLootTable(i, item));

                                    context.getSource().sendFeedback(Text.literal("The item appears in " + lootTables.count() + " loot tables."));


                                    return Command.SINGLE_SUCCESS;
                                })))
                .executes(context -> {
                    context.getSource().sendFeedback(Text.literal(helpMessage));
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static boolean isInLootTable(RegistryKey<LootTable> lootTableKey, Item item) {
        LootTable table = Loot_locatorClient.INSTANCE.lootTableHelper.getLootTable(lootTableKey);
        for (LootPool pool : ((ILootTable) table).lootLocator$getPools()) {
            for (LootPoolEntry entry : pool.entries) {
                if (entry instanceof ItemEntry itemEntry) {
                    if (((IItemEntry) itemEntry).lootLocator$getItem().value() == item) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
