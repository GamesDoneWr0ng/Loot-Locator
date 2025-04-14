package org.gamesdonewr0ng.loot_locator.client.util;

import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public interface IItemEntry {
    RegistryEntry<Item> lootLocator$getItem();
}
