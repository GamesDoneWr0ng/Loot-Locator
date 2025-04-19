package org.gamesdonewr0ng.loot_locator.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
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
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import org.gamesdonewr0ng.loot_locator.client.LootLocatorClient;
import org.gamesdonewr0ng.loot_locator.client.searchers.SearcherManager;
import org.gamesdonewr0ng.loot_locator.client.util.IItemEntry;
import org.gamesdonewr0ng.loot_locator.client.util.ILootTable;
import org.gamesdonewr0ng.loot_locator.client.util.ItemHelper;

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
                                    Item mcItem = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                    Stream<RegistryKey<LootTable>> lootTables = LootTables.getAll().stream().filter(i -> isInLootTable(i, mcItem));

                                    context.getSource().sendFeedback(Text.literal("The item appears in " + lootTables.count() + " loot tables."));
                                    com.seedfinding.mcfeature.loot.item.Item item = ItemHelper.getItem(mcItem);

                                    BlockPos pos = context.getSource().getEntity().getBlockPos();
                                    SearcherManager searcherManager = new SearcherManager(lootTables, new BPos(pos.getX(), pos.getY(), pos.getZ()), context.getSource().getWorld().getDimensionEntry().getKey().get());

                                    return Command.SINGLE_SUCCESS;
                                }))
                        .executes(context -> {
                            context.getSource().sendFeedback(Text.literal("Please specify a item to search for."));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(ClientCommandManager.literal("structure")
                        .then(ClientCommandManager.argument("structure", StringArgumentType.string())
                                .suggests(FinderArguments.STRUCTURES)
                                .executes(context -> {
                                    String structure = StringArgumentType.getString(context, "structure");
                                    BlockPos pos = context.getSource().getEntity().getBlockPos();
                                    SearcherManager searcherManager = new SearcherManager(structure, new BPos(pos.getX(), pos.getY(), pos.getZ()), context.getSource().getWorld().getDimensionEntry().getKey().get());

                                    CPos res = searcherManager.getNextPos();

                                    context.getSource().sendFeedback(
                                            Text.literal(String.format("There is a %s at %d ~ %d", structure, res.getX()<<4, res.getZ()<<4))
                                                    .setStyle(Style.EMPTY.withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/tp @s %d ~ %d", res.getX()<<4, res.getZ()<<4)))));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .executes(context -> {
                    context.getSource().sendFeedback(Text.literal(helpMessage));
                    return Command.SINGLE_SUCCESS;
                }));
    }

    private static boolean isInLootTable(RegistryKey<LootTable> lootTableKey, Item item) {
        LootTable table = LootLocatorClient.INSTANCE.lootTableHelper.getLootTable(lootTableKey);
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
