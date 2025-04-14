package org.gamesdonewr0ng.loot_locator.client.mixin;

import net.minecraft.item.Item;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.entry.RegistryEntry;
import org.gamesdonewr0ng.loot_locator.client.util.IItemEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntry.class)
public class ItemEntryMixin implements IItemEntry {
    @Final
    @Shadow
    private RegistryEntry<Item> item;

    public RegistryEntry<Item> lootLocator$getItem() {
        return item;
    }
}
